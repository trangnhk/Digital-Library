from app.model.referenced_book import ReferencedBook
from app.model.conversation_state import ConversationState
from app.services.query_rewriting_service import QueryRewritingService

def test_normal_query():
    service = QueryRewritingService()

    query = "books about vampires"

    result = service.rewrite(
        user_query=query,
        referenced_book=None,
    )

    assert result == query

    print("✓ Normal query passed")


def test_follow_up_with_referenced_book():
    service = QueryRewritingService()

    book = ReferencedBook(
        book_id=123,
        title="Dracula",
    )

    query = "Who wrote it?"

    result = service.rewrite(user_query=query, referenced_book=book,)
    

    expected = "Who wrote the book 'Dracula'?"

    assert result == expected

    print("✓ Follow-up with referenced book passed")


def test_follow_up_without_referenced_book():
    service = QueryRewritingService()

    query = "Who wrote it?"

    result = service.rewrite(
        user_query=query,
        referenced_book=None,
    )

    assert result == query

    print("✓ Follow-up without referenced book passed")


def test_empty_query():
    service = QueryRewritingService()

    try:
        service.rewrite(
            user_query="",
            referenced_book=None,
        )

        assert False, (
            "Expected ValueError for empty query"
        )

    except ValueError:
        print("✓ Empty query passed")

def test_complete_conversation():
    service = QueryRewritingService()

    # TURN 1
    # User does not know the book title
    user_query_1 = ("I'm looking for a vampire book about an aristocratic vampire.")

    # Simulate the book found by the retrieval pipeline.
    referenced_book = ReferencedBook(book_id=123,title="Dracula",)

    # Simulate conversation state after AI
    # identifies/recommends the book.
    state = ConversationState(last_referenced_book=referenced_book)

    print("\nTURN 1\n")

    print("User:")
    print(user_query_1)

    print(f"\nAI found: {state.last_referenced_book.title}")

    # TURN 2
    # User asks a follow-up question

    user_query_2 = "Who wrote it?"

    rewritten_query = service.rewrite(
        user_query=user_query_2,
        referenced_book=(state.last_referenced_book),)

    print("\nTURN 2\n")
    print("User:")
    print(user_query_2)

    print(f"\nReferenced book: {state.last_referenced_book.title}")

    print(f"\nRewritten query: {rewritten_query}")

    expected = ("Who wrote the book 'Dracula'?")

    assert rewritten_query == expected

    print("\n✓ Complete conversation test passed")

def main():
    test_normal_query()
    test_follow_up_with_referenced_book()
    test_follow_up_without_referenced_book()
    test_empty_query()
    test_complete_conversation()

    print("\nAll QueryRewritingService tests passed.")


if __name__ == "__main__":
    main()