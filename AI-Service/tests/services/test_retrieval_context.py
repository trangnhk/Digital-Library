from app.config.settings import DEFAULT_TOP_K

from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService
from app.services.retrieval_service import RetrievalService
from app.services.context_service import ContextService


def main():

    embedding_service = EmbeddingService()

    faiss_service = FaissService()

    metadata_service = MetadataService()

    retrieval_service = RetrievalService(
        embedding_service=embedding_service,
        faiss_service=faiss_service,
        metadata_service=metadata_service,
    )

    context_service = ContextService()

    query = "books about vampires"

    # 1. Retrieval

    results = retrieval_service.retrieve(
        query=query,
        top_k=DEFAULT_TOP_K,
    )

    print("\n========== RETRIEVAL ==========")

    print(results)
    print(results.columns.tolist())

    # 2. Context

    context = (
        context_service
        .build_retrieved_context(results)
    )

    print("\n========== CONTEXT ==========")

    print(context)


if __name__ == "__main__":
    main()