from typing import List, Dict

from pydantic import BaseModel

from app.model.referenced_book import ReferencedBook
from app.model.conversation_state import ConversationState


class ChatResponse(BaseModel):
    answer: str
    referenced_book: ReferencedBook | None = None
    conversation_history: List[Dict[str, str]]
    conversation_state: ConversationState
    