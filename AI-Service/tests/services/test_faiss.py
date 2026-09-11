from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.config.settings import (
    EMBEDDING_DIM,
    DEFAULT_TOP_K,
)


def main():
    embedding_service = EmbeddingService()
    faiss_service = FaissService()

    query = "books about vampires"

    embedding = embedding_service.encode_query(query)

    print("========== EMBEDDING ==========")
    print("Device:", embedding_service.device)
    print("Embedding dimension:", len(embedding))

    print("\n========== FAISS INDEX ==========")
    print("Index dimension:", faiss_service.index.d)
    print("Total vectors:", faiss_service.index.ntotal)
    print("Is trained:", faiss_service.index.is_trained)
    print("Nprobe:", faiss_service.ivf_index.nprobe)

    distances, indices = faiss_service.search(
        embedding,
        DEFAULT_TOP_K
    )

    print("\n========== SEARCH RESULT ==========")
    print("Distances:", distances)
    print("FAISS IDs:", indices)


if __name__ == "__main__":
    main()