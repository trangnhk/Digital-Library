from dataclasses import dataclass
from typing import Optional

from app.model.referenced_book import ReferencedBook


@dataclass
class ConversationState:
    last_referenced_book: Optional[ReferencedBook] = None