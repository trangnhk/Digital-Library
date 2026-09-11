from typing import Optional

import pandas as pd

from app.config.settings import DEFAULT_TOP_K, MAX_DISTANCE

from app.services.embedding_service import EmbeddingService
from app.services.faiss_service import FaissService
from app.services.metadata_service import MetadataService

class RetrievalService:
    def __init__(self, embedding_service: EmbeddingService | None = None, faiss_service: FaissService | None = None, metadata_service: MetadataService | None = None,):

        if embedding_service is None:
            embedding_service = EmbeddingService()

        if faiss_service is None:
            faiss_service = FaissService()

        if metadata_service is None:
            metadata_service = MetadataService()

        self.embedding_service = embedding_service
        self.faiss_service = faiss_service
        self.metadata_service = metadata_service

    def retrieve(self, query: str, top_k: int = DEFAULT_TOP_K) -> pd.DataFrame:
        """
        Retrieve metadata for the top_k most relevant documents based on the query.
        """
        if not query:
            raise ValueError("Query must be a non-empty string.")

        query = query.strip()

        if top_k <= 0:
            raise ValueError("top_k must be a positive integer.")

        # Step 1: Convert query to embedding
        query_embedding = self.embedding_service.encode_query(query)

        # Step 2: Search FAISS index
        distances, indices = self.faiss_service.search(query_embedding, top_k)

        # Step 3: Retrieve metadata for the found FAISS IDs
        metadata_df = self.metadata_service.get_metadata_by_faiss_id(indices)

        if metadata_df.empty:
            return metadata_df  # Return empty DataFrame if no metadata found

        score_mapping = {
            int(faiss_id): float(score)
            for faiss_id, score
            in zip(indices, distances)
        }

        rank_mapping = {
            int(faiss_id): rank
            for rank, faiss_id
            in enumerate(indices, start=1)
        }

        metadata_df['score'] = metadata_df['faiss_id'].map(score_mapping)
        metadata_df['rank'] = metadata_df['faiss_id'].map(rank_mapping)

        # Step 5
        # metadata_df = metadata_df[metadata_df['score'] <= MAX_DISTANCE] 

        metadata_df = (metadata_df.sort_values(by='rank').reset_index(drop=True))

        print("\nRetrieved results:")
        print(metadata_df)

        print("=" * 70)

        for _, row in metadata_df.iterrows():
            print(
                f"book_id={row['book_id']} | "
                f"title={row['title']} | "
                f"score={row['score']}"
            )

        return metadata_df