from sentence_transformers import SentenceTransformer
from app.config.settings import EMBEDDING_MODEL_NAME, EMBEDDING_DIM

class EmbeddingService:
    def __init__(self):
        self.device = "cuda" if self._cuda_available() else "cpu"
        self.model = SentenceTransformer(EMBEDDING_MODEL_NAME, device=self.device)
        self.embedding_dim = EMBEDDING_DIM

    @staticmethod
    def _cuda_available():
        try:
            import torch
            return torch.cuda.is_available()
        except ImportError:
            return False

    def encode_query(self, query: str):
        """
        Encode a query string into an embedding vector.
        """
        if not query or not query.strip():
            raise ValueError("Query string cannot be empty or whitespace.")
        
        embedding = self.model.encode(query, normalize_embeddings=True)
        
        if len(embedding) != self.embedding_dim:
            raise ValueError(f"Expected embedding dimension {self.embedding_dim}, got {len(embedding)}")
        return embedding