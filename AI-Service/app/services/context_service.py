import pandas as pd

class ContextService:
    def build_retrieved_context(self,results: pd.DataFrame,) -> str:

        if results is None:
            return ""

        if results.empty:
            return ""

        contexts = []

        for _, row in results.iterrows():

            book_id = row["book_id"]
            title = row["title"]
            author = row["author"]
            content = row["chunk_text"]

            context = (
                f"BOOK ID: {book_id}\n"
                f"TITLE: {title}\n"
                f"AUTHOR: {author}\n\n"
                f"CONTENT:\n{content}"
            )

            contexts.append(context)

        return "\n\n".join(contexts)