from typing import Optional
from pydantic import BaseModel

class ReferencedBook(BaseModel):
    book_id: Optional[int] = None
    title: Optional[str] = None
