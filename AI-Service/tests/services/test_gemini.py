import pandas as pd

from app.services.gemini_service import GeminiService
from app.services.context_service import ContextService

from app.prompt.rag_prompt import build_rag_prompt


def main():

    # ==================================================
    # 1. CREATE TEST RETRIEVED RESULTS
    # ==================================================

    retrieved_results = pd.DataFrame([
        {
            "faiss_id": 17682,
            "chunk_id": "4280_1",
            "book_id": 123,
            "title": "Dracula",
            "author": "Bram Stoker",
            "publisher": "Archibald Constable",
            "chunk_text": (
                "Dracula is a gothic horror novel "
                "written by Bram Stoker."
            ),
            "score": 0.92,
            "rank": 1,
        },
        {
            "faiss_id": 36184,
            "chunk_id": "4280_2",
            "book_id": 456,
            "title": "Interview with the Vampire",
            "author": "Anne Rice",
            "publisher": "Alfred A. Knopf",
            "chunk_text": (
                "Interview with the Vampire is a "
                "vampire fiction novel."
            ),
            "score": 0.87,
            "rank": 2,
        },
        {
            "faiss_id": 44871,
            "chunk_id": "4280_3",
            "book_id": 789,
            "title": "Frankenstein",
            "author": "Mary Shelley",
            "publisher": "Lackington",
            "chunk_text": (
                "Frankenstein is a gothic novel "
                "written by Mary Shelley."
            ),
            "score": 0.81,
            "rank": 3,
        },
    ])

    print("=" * 60)
    print("RETRIEVED RESULTS")
    print("=" * 60)

    print(
        retrieved_results[
            [
                "book_id",
                "title",
                "author",
                "score",
                "rank",
            ]
        ]
    )

    # ==================================================
    # 2. BUILD RETRIEVED CONTEXT
    # ==================================================

    context_service = ContextService()

    retrieved_context = (
        context_service
        .build_retrieved_context(
            retrieved_results
        )
    )

    print("\n")
    print("=" * 60)
    print("RETRIEVED CONTEXT")
    print("=" * 60)

    print(retrieved_context)

    # ==================================================
    # 3. BUILD RAG PROMPT
    # ==================================================

    user_query = (
        "I'm looking for a vampire horror book."
    )

    conversation_history = []

    prompt = build_rag_prompt(
        user_query=user_query,
        retrieved_context=retrieved_context,
        history_text=conversation_history,
    )

    # ==================================================
    # 4. CALL GEMINI
    # ==================================================

    print("\n")
    print("=" * 60)
    print("CALLING GEMINI")
    print("=" * 60)

    gemini_service = GeminiService()

    result = (
        gemini_service
        .generate_response(prompt)
    )

    # ==================================================
    # 5. PRINT GEMINI RESPONSE
    # ==================================================

    print("\n")
    print("=" * 60)
    print("GEMINI RESPONSE")
    print("=" * 60)

    print("Answer:")
    print(result.answer)

    print("\nReferenced book:")

    if result.referenced_book is None:

        print("None")

    else:

        print(
            f"book_id: "
            f"{result.referenced_book.book_id}"
        )

        print(
            f"title: "
            f"{result.referenced_book.title}"
        )

    # ==================================================
    # 6. VALIDATE REFERENCED BOOK
    # ==================================================

    print("\n")
    print("=" * 60)
    print("REFERENCED BOOK VALIDATION")
    print("=" * 60)

    referenced_book = (
        result.referenced_book
    )

    if referenced_book is None:

        print(
            "No referenced book returned by Gemini."
        )

        return

    book_id = referenced_book.book_id

    print(
        f"Gemini book_id : {book_id}"
    )

    retrieved_book_ids = set(
        retrieved_results[
            "book_id"
        ]
        .dropna()
        .tolist()
    )

    print(
        f"Retrieved book_ids : "
        f"{retrieved_book_ids}"
    )

    # --------------------------------------------------
    # Check book_id
    # --------------------------------------------------

    if book_id in retrieved_book_ids:

        print(
            "✓ book_id exists in retrieved_results"
        )

    else:

        print(
            "✗ INVALID: book_id does NOT exist "
            "in retrieved_results"
        )

        return

    # --------------------------------------------------
    # Check title
    # --------------------------------------------------

    matched = retrieved_results[
        retrieved_results["book_id"] == book_id
    ]

    if matched.empty:

        print(
            "✗ No matching book found."
        )

        return

    library_title = str(
        matched.iloc[0]["title"]
    ).strip()

    gemini_title = str(
        referenced_book.title
    ).strip()

    print(
        f"Gemini title   : {gemini_title}"
    )

    print(
        f"Library title  : {library_title}"
    )

    if gemini_title == library_title:

        print(
            "✓ referenced_book title matches "
            "library metadata"
        )

    else:

        print(
            "✗ WARNING: referenced_book title "
            "does NOT match library metadata"
        )


if __name__ == "__main__":
    main()