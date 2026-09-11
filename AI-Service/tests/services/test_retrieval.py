from app.config.settings import DEFAULT_TOP_K

from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService
from app.services.retrieval_service import RetrievalService


def main():

    embedding_service = EmbeddingService()
    faiss_service = FaissService()
    metadata_service = MetadataService()

    # Create RetrievalService

    retrieval_service = RetrievalService(
        embedding_service=embedding_service,
        faiss_service=faiss_service,
        metadata_service=metadata_service,
    )

    # Query
    query = "books about vampires"

    # Retrieval
    results = retrieval_service.retrieve(query=query,top_k=DEFAULT_TOP_K,)

    print("\nQuery:")
    print(query)

    print("\nRetrieved results:")
    print(results)

    print("\nColumns:")
    print(results.columns.tolist())

    print("\nNumber of results:")
    print(len(results))


if __name__ == "__main__":
    main()