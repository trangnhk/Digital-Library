from fastapi import APIRouter, Request

from app.apis.schemas import ChatResponse, ChatRequest

from app.services.chatbot_service import ChatbotService
from app.services.conversation_service import ConversationService
from app.services.query_rewriting_service import QueryRewritingService
from app.services.retrieval_service import RetrievalService
from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService
from app.services.context_service import ContextService
from app.services.gemini_service import GeminiService

router = APIRouter(prefix="/api", tags=["Chat"])

# Initialize services
conversation_service = ConversationService()
query_rewriting_service = QueryRewritingService()
embedding_service = EmbeddingService()
faiss_service = FaissService()
metadata_service = MetadataService()
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

"""API POST /chat"""
@router.post("/chat", response_model=ChatResponse)

def chat(request: ChatRequest):
    result = chatbot_service.chat(
        user_query=request.message,
        conversation_history=request.conversation_history,
        conversation_state=request.conversation_state,
        top_k=request.top_k,
    )

    return ChatResponse(
        answer=result["answer"],
        referenced_book=(result["conversation_state"].last_referenced_book),
        conversation_history=result["conversation_history"],
        conversation_state=result["conversation_state"],
    )

@router.post("/chat-debug")
async def chat_debug(request: Request):

    print("\n" + "=" * 70)
    print("REQUEST RECEIVED FROM SPRING")
    print("=" * 70)

    print("\n--- HEADERS ---")

    for key, value in request.headers.items():
        print(f"{key}: {value}")

    print("\n--- RAW BODY ---")

    body = await request.body()

    print(body.decode("utf-8"))

    print("\n--- BODY LENGTH ---")

    print(len(body))

    print("=" * 70)

    return {
        "received": True,
        "body": body.decode("utf-8"),
        "body_length": len(body)
    }
