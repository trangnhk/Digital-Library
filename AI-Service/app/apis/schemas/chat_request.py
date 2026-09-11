from typing import List, Dict, Optional
from pydantic import BaseModel, Field

from app.model.conversation_state import ConversationState
from app.config.settings import DEFAULT_TOP_K

class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, description="Current message from user.")

    conversation_history: List[Dict[str, str]] = Field(
        default_factory=list,
        description="Previous conversation messages."
    )

    conversation_state: Optional[ConversationState] = Field(default=None, description="Current conversational state")

    top_k: int = Field(default=DEFAULT_TOP_K, ge=1, description="Number of documents tp retrieve")