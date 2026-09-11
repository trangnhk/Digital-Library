from dataclasses import dataclass
from typing import Optional
from pydantic import BaseModel

from app.model.referenced_book import ReferencedBook


@dataclass
class ChatbotResponse(BaseModel):
    answer: str
    referenced_book: Optional[ReferencedBook] = None