import faiss
from app.config.settings import FAISS_INDEX_PATH, EMBEDDING_DIM, DEFAULT_NPROBE

class FaissService:
    def __init__(self):
        self.index = faiss.read_index(str(FAISS_INDEX_PATH)) #load the FAISS index from the specified path
        self._validate_index()
        self._configure_nprobe()

    def _validate_index(self):
        if self.index.d != EMBEDDING_DIM:
            raise ValueError(f"Expected index dimension {EMBEDDING_DIM}, got {self.index.d}")

        if self.index.ntotal <= 0:
            raise ValueError("FAISS index is empty. Please ensure the index is built and populated.")

        if not self.index.is_trained:
            raise ValueError("FAISS index is not trained. Please ensure the index is trained before use.")

    def _configure_nprobe(self):
        try:
            ivf_index = faiss.extract_index_ivf(self.index)

        except RuntimeError as exc:
            raise ValueError("The loaded FAISS index is not an IVF-based index.") from exc

        ivf_index.nprobe = DEFAULT_NPROBE
        self.ivf_index = ivf_index

    def search(self, query_embedding, top_k: int):
        """
        Perform a search on the FAISS index using the provided query embedding.
        Returns the indices and distances of the top_k nearest neighbors.
        """

        if query_embedding is None:
            raise ValueError("Query embedding cannot be None.")

        if top_k <= 0:
            raise ValueError("top_k must be a positive integer.")

        query_vector = query_embedding.reshape(1, -1).astype('float32')

        if query_vector.shape[1] != self.index.d:
            raise ValueError(f"Expected query embedding dimension {self.index.d}, got {query_vector.shape[1]}")

        distances, indices = self.index.search(query_vector, top_k)

        return distances[0], indices[0]
        