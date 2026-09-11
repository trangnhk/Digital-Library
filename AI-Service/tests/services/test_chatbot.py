from app.services.conversation_service import ConversationService
from app.services.query_rewriting_service import QueryRewritingService
from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService
from app.services.retrieval_service import RetrievalService
from app.services.context_service import ContextService
from app.services.gemini_service import GeminiService
from app.services.chatbot_service import ChatbotService

from app.model.conversation_state import ConversationState


def main():

    print("=" * 70)
    print("INITIALIZING CHATBOT SERVICES")
    print("=" * 70)

    conversation_service = ConversationService()
    query_rewriting_service = QueryRewritingService()
    embedding_service = EmbeddingService()
    faiss_service = FaissService()
    metadata_service = MetadataService()
    
        # Create RetrievalService
    
    retrieval_service = RetrievalService(
        embedding_service=embedding_service,
        faiss_service=faiss_service,
        metadata_service=metadata_service,
    )
    context_service = ContextService()
    gemini_service = GeminiService()

    chatbot_service = ChatbotService(
        conversation_service=conversation_service,
        query_rewriting_service=query_rewriting_service,
        retrieval_service=retrieval_service,
        context_service=context_service,
        gemini_service=gemini_service,
    )

    # ==========================================================
    # SHARED CONVERSATION
    # ==========================================================

    conversation_history = []

    conversation_state = ConversationState()

    # ==========================================================
    # TURN 1
    # ==========================================================

    print("\n")
    print("=" * 70)
    print("TURN 1")
    print("=" * 70)

    user_query_1 = (
        "I'm looking for a book about vampires."
    )

    print("\nUser:")
    print(user_query_1)

    result_1 = chatbot_service.chat(
        user_query=user_query_1,
        conversation_history=conversation_history,
        conversation_state=conversation_state,
    )

    # ----------------------------------------------------------
    # Print rewritten query
    # ----------------------------------------------------------

    print("\nRewritten query:")
    print(result_1["rewritten_query"])

    # ----------------------------------------------------------
    # Print retrieved results
    # ----------------------------------------------------------

    print("\nRetrieved results:")

    retrieved_results_1 = result_1["retrieved_results"]

    if retrieved_results_1 is None:
        print("None")
    else:
        print(
            retrieved_results_1[
                [
                    "book_id",
                    "title",
                    "author",
                    "score",
                    "rank",
                ]
            ].to_string(index=False)
        )

    # ----------------------------------------------------------
    # Print Gemini answer
    # ----------------------------------------------------------

    print("\nAssistant:")
    print(result_1["answer"])

    # ----------------------------------------------------------
    # Print referenced book
    # ----------------------------------------------------------

    print("\nReferenced book:")

    referenced_book_1 = (
        result_1[
            "conversation_state"
        ].last_referenced_book
    )

    if referenced_book_1 is None:

        print("None")

    else:

        print(
            f"book_id: {referenced_book_1.book_id}"
        )

        print(
            f"title: {referenced_book_1.title}"
        )

    # ----------------------------------------------------------
    # IMPORTANT:
    # Preserve state returned from turn 1
    # ----------------------------------------------------------

    conversation_history = (
        result_1["conversation_history"]
    )

    conversation_state = (
        result_1["conversation_state"]
    )

    # ==========================================================
    # TURN 2
    # ==========================================================

    print("\n")
    print("=" * 70)
    print("TURN 2")
    print("=" * 70)

    user_query_2 = "Who wrote it?"

    print("\nUser:")
    print(user_query_2)

    print("\nReferenced book BEFORE rewriting:")

    if conversation_state.last_referenced_book is None:

        print("None")

    else:

        print(
            f"book_id: "
            f"{conversation_state.last_referenced_book.book_id}"
        )

        print(
            f"title: "
            f"{conversation_state.last_referenced_book.title}"
        )

    result_2 = chatbot_service.chat(
        user_query=user_query_2,
        conversation_history=conversation_history,
        conversation_state=conversation_state,
    )

    # ----------------------------------------------------------
    # Print rewritten query
    # ----------------------------------------------------------

    print("\nRewritten query:")
    print(result_2["rewritten_query"])

    # ----------------------------------------------------------
    # Print retrieved results
    # ----------------------------------------------------------

    print("\nRetrieved results:")

    retrieved_results_2 = result_2["retrieved_results"]

    if retrieved_results_2 is None:
        print("None")
    else:
        print(
            retrieved_results_2[
                [
                    "book_id",
                    "title",
                    "author",
                    "score",
                    "rank",
                ]
            ].to_string(index=False)
        )

    # ----------------------------------------------------------
    # Print Gemini answer
    # ----------------------------------------------------------

    print("\nAssistant:")
    print(result_2["answer"])

    # ----------------------------------------------------------
    # Print final state
    # ----------------------------------------------------------

    print("\nFinal referenced book:")

    referenced_book_2 = (
        result_2[
            "conversation_state"
        ].last_referenced_book
    )

    if referenced_book_2 is None:

        print("None")

    else:

        print(
            f"book_id: {referenced_book_2.book_id}"
        )

        print(
            f"title: {referenced_book_2.title}"
        )

    # ----------------------------------------------------------
    # Print conversation history
    # ----------------------------------------------------------

    print("\nConversation history:")

    for message in result_2["conversation_history"]:

        print(
            f"[{message['role']}] "
            f"{message['content']}"
        )

    print("\n")
    print("=" * 70)
    print("CHATBOT TEST COMPLETED")
    print("=" * 70)


if __name__ == "__main__":
    main()