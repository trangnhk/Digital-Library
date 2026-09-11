from typing import Dict, List, Optional

from app.model.conversation_state import ConversationState
from app.model.referenced_book import ReferencedBook

from app.services.conversation_service import ConversationService
from app.services.query_rewriting_service import QueryRewritingService
from app.services.retrieval_service import RetrievalService
from app.services.context_service import ContextService
from app.services.gemini_service import GeminiService

from app.config.settings import DEFAULT_TOP_K

from app.prompt.rag_prompt import build_rag_prompt

class ChatbotService:

    def __init__(
        self,
        conversation_service: ConversationService,
        query_rewriting_service: QueryRewritingService,
        retrieval_service: RetrievalService,
        context_service: ContextService,
        gemini_service: GeminiService,
    ):
        self.conversation_service = conversation_service
        self.query_rewriting_service = query_rewriting_service
        self.retrieval_service = retrieval_service
        self.context_service = context_service
        self.gemini_service = gemini_service

    def chat(self, user_query: str, conversation_history: Optional[List[Dict[str, str]]] = None, conversation_state: Optional[ConversationState] = None, top_k: int = DEFAULT_TOP_K,) -> Dict:

        # Validate
        if user_query is None:
            raise ValueError("user_query cannot be None.")

        original_query = str(user_query).strip()

        if not original_query:
            raise ValueError("user_query cannot be empty.")

        if conversation_history is None:
            conversation_history = []

        if conversation_state is None:
            conversation_state = (ConversationState())

        conversation_history = (self.conversation_service.get_recent_history(conversation_history))
        history_text = (self.conversation_service .format_conversation_history( conversation_history))

        referenced_book = (conversation_state.last_referenced_book)

        rewritten_query = (self.query_rewriting_service.rewrite(user_query=original_query,referenced_book=referenced_book,))

        # # RETRIEVAL
        # try:
        #     retrieved_results = (self.retrieval_service.retrieve(query=rewritten_query, top_k=top_k,))

        # except Exception as e:
        #     raise RuntimeError("Retrieval failed.") from e

        # # BUILD RETRIEVED CONTEXT
        # try:
        #     retrieved_context = (self.context_service.build_retrieved_context(retrieved_results))

        # except Exception as e:
        #     raise RuntimeError("Building retrieved context failed.") from e

        # RETRIEVAL
        try:
            retrieved_results = (self.retrieval_service.retrieve(query=rewritten_query, top_k=top_k,))

        except Exception as e:
            raise RuntimeError("Retrieval failed.") from e

        # NO RELEVANT DOCUMENT
        if retrieved_results is None or retrieved_results.empty:

            answer = ("I couldn't find any relevant books or documents in the library for your question.")

            # The current turn does not reference any book.
            conversation_state.last_referenced_book = None

            # Store original user query
            self.conversation_service.add_message(
                conversation=conversation_history,
                role="user",
                content=original_query,
            )

            # Store assistant response
            self.conversation_service.add_message(
                conversation=conversation_history,
                role="assistant",
                content=answer,
            )

            conversation_history = (self.conversation_service.get_recent_history(conversation_history))

            return {
                "original_query": original_query,
                "rewritten_query": rewritten_query,
                "retrieved_results": retrieved_results,
                "retrieved_context": "",
                "answer": answer,
                "conversation_history": conversation_history,
                "conversation_state": conversation_state,
            }

        # BUILD RETRIEVED CONTEXT
        try:
            retrieved_context = (self.context_service.build_retrieved_context(retrieved_results))

        except Exception as e:
            raise RuntimeError("Building retrieved context failed.") from e

        """
        BUILD RAG PROMPT: Gemini receives ORIGINAL query
        """

        prompt = build_rag_prompt(
            user_query=original_query,
            retrieved_context=retrieved_context,
            history_text=history_text,
        )

        try:
            gemini_result = (self.gemini_service.generate_response(prompt))

        except Exception as e:
            raise RuntimeError("Gemini RAG generation failed.") from e

        answer = gemini_result.answer
        gemini_referenced_book = (gemini_result.referenced_book)

        # Validate referenced book
        validated_referenced_book = (self._validate_referenced_book(referenced_book=gemini_referenced_book, retrieved_results=retrieved_results))

        # Update referenced book
        self._update_referenced_book(conversation_state=conversation_state, referenced_book=(validated_referenced_book),)

        # UPDATE Conversation history --> Store ORIGINAL query
        self.conversation_service.add_message(
            conversation=conversation_history,
            role="user",
            content=original_query,
        )

        self.conversation_service.add_message(
            conversation=conversation_history,
            role="assistant",
            content=answer,
        )

        conversation_history = (self.conversation_service.get_recent_history(conversation_history))
        

        return {
            "original_query": original_query,

            "rewritten_query": rewritten_query,

            "retrieved_results": retrieved_results,

            "retrieved_context": retrieved_context,

            "answer": answer,

            "conversation_history": (conversation_history),

            "conversation_state": (conversation_state),
        }

    def _update_referenced_book(self, conversation_state: ConversationState, referenced_book: Optional[ReferencedBook],) -> None:

        if referenced_book is None:
            conversation_state.last_referenced_book = None
            return

        if not referenced_book.title:
            return

        conversation_state.last_referenced_book = (referenced_book)

    def _validate_referenced_book(self, referenced_book: Optional[ReferencedBook], retrieved_results,) -> Optional[ReferencedBook]:

        if referenced_book is None:
            return None

        if retrieved_results is None:
            return None

        if retrieved_results.empty:
            return None

        if "book_id" not in retrieved_results.columns:
            return None

        if "title" not in retrieved_results.columns:
            return None

        print(retrieved_results.columns.tolist())

        book_id = referenced_book.book_id

        if book_id is None:
            return None

        matched = retrieved_results[retrieved_results["book_id"] == book_id]

        if matched.empty:
            return None

        library_title = str(matched.iloc[0]["title"]).strip()

        if not library_title:
            return None

        return ReferencedBook(
            book_id=book_id,
            title=library_title,
        )

