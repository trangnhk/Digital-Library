from typing import Optional

from app.model.referenced_book import ReferencedBook


class QueryRewritingService:
    """
    Local rule-based query rewriting service.

    This service DOES NOT call Gemini.
    """

    REFERENCE_PATTERNS = [
        "this book",
        "that book",
        "the book",

        "this novel",
        "that novel",
        "the novel",

        "this document",
        "that document",
        "the document",

        "this story",
        "that story",

        "it",
    ]



    def is_follow_up_query(self, user_query: str, ) -> bool:
        """
        Detect simple conversational follow-up queries.
        """

        query = user_query.strip().lower()

        # follow_up_patterns = [
        #     "who wrote it",
        #     "who wrote this",
        #     "who is the author",
        #     "who's the author",
        #     "who wrote the book",

        #     "what is it about",
        #     "what's it about",
        #     "tell me more",
        #     "tell me about it",

        #     "when was it published",
        #     "when was this published",
        #     "when did it come out",

        #     "what genre is it",
        #     "what is the genre",

        #     "is it part of a series",
        #     "what series is it in",

        #     "who are the characters",
        #     "who is the main character",
        # ]

        return any(
            pattern in query
            for pattern in self.REFERENCE_PATTERNS
        )

    def rewrite_query_locally(self, user_query: str, referenced_book: Optional[ReferencedBook],) -> str:
        """
        Rewrite a follow-up query into a self-contained
        retrieval query using local rule-based logic.
        """

        if user_query is None:
            raise ValueError("user_query cannot be None.")

        user_query = str(user_query).strip()

        if not user_query:
            raise ValueError("user_query cannot be empty.")

        # Normal query -> no rewriting
        if not self.is_follow_up_query(user_query):
            return user_query

        # Follow-up query but no book context
        if referenced_book is None:
            return user_query

        book_title = str(referenced_book.title).strip()

        # # Invalid book context
        # if not book_title:
        #     return user_query

        # query_lower = user_query.lower()

        # # Author-related follow-up
        # if any(
        #     phrase in query_lower
        #     for phrase in [
        #         "who wrote",
        #         "who is the author",
        #         "who's the author",
        #     ]
        # ):
        #     return (f"Who wrote the book '{book_title}'?")

        # # Publication-related follow-up
        # if any(
        #     phrase in query_lower
        #     for phrase in [
        #         "when was it published",
        #         "when was this published",
        #         "when did it come out",
        #     ]
        # ):
        #     return (f"When was the book '{book_title}' published?")

        # # Description-related follow-up
        # if any(
        #     phrase in query_lower
        #     for phrase in [
        #         "what is it about",
        #         "what's it about",
        #         "tell me more",
        #         "tell me about it",
        #     ]
        # ):
        #     return (f"What is the book '{book_title}' about?")

        # # Genre-related follow-up
        # if "genre" in query_lower:
        #     return (f"What genre is the book '{book_title}'?")

        # # Series-related follow-up
        # if "series" in query_lower:
        #     return (f"What series is the book '{book_title}' part of?")

        # # Character-related follow-up
        # if "character" in query_lower:
        #     return (f"Who are the main characters in '{book_title}'?")

        # Default
        return (f"{user_query} The user is referring to the book '{book_title}'.")

    def rewrite(self, user_query: str, referenced_book: Optional[ReferencedBook],) -> str:
        """
        Main public method used by ChatbotService.
        """

        if user_query is None:
            raise ValueError("user_query cannot be None.")

        user_query = str(user_query).strip()

        if not user_query:
            raise ValueError("user_query cannot be empty.")

        return self.rewrite_query_locally(user_query=user_query, referenced_book=referenced_book,)