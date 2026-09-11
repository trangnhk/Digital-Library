from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService

from app.config.settings import DEFAULT_TOP_K

import pandas as pd


def main():

    embedding_service = EmbeddingService()
    faiss_service = FaissService()
    metadata_service = MetadataService()

    query = "books about vampires"

    # 1. Query → embedding
    embedding = embedding_service.encode_query(query)

    # 2. Embedding → FAISS
    distances, indices = faiss_service.search(embedding,DEFAULT_TOP_K)

    print("FAISS IDs:")
    print(indices)

    # 3. FAISS IDs → metadata
    metadata = (metadata_service.get_metadata_by_faiss_id(indices))

    print("\nMetadata:")
    print(metadata)

    test_ids = [
        17682,
        36184,
        44871,
        37175,
        28875,
    ]


    for faiss_id in test_ids:

        metadata_file = (
            metadata_service
            .find_metadata_file(faiss_id)
        )

        df = pd.read_parquet(
            metadata_file,
            columns=["faiss_id"]
        )

        matched = df[
            df["faiss_id"] == faiss_id
        ]

        print(
            f"ID={faiss_id}, "
            f"file={metadata_file.name}, "
            f"matches={len(matched)}"
        )


if __name__ == "__main__":
    main()