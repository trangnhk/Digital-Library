import pandas as pd

from app.services.context_service import ContextService


def main():

    results = pd.DataFrame(
        [
            {
                "book_id": 1,
                "title": "Dracula",
                "author": "Bram Stoker",
                "chunk_text": (
                    "Jonathan Harker travels to Transylvania."
                ),
            },
            {
                "book_id": 2,
                "title": "Interview with the Vampire",
                "author": "Anne Rice",
                "chunk_text": (
                    "The story follows a vampire named Louis."
                ),
            },
        ]
    )

    context_service = ContextService()

    context = (
        context_service
        .build_retrieved_context(results)
    )

    print(context)


if __name__ == "__main__":
    main()