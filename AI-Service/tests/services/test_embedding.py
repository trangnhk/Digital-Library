from app.services.embedding_service import EmbeddingService


def main():
    service = EmbeddingService()

    query = "books about vampires"

    embedding = service.encode_query(query)

    print("Device:", service.device)
    print("Embedding type:", type(embedding))
    print("Embedding dimension:", len(embedding))
    print("First 5 values:", embedding[:5])


if __name__ == "__main__":
    main()