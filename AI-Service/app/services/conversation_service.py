from typing import Dict, List, Optional
from app.config.settings import MAX_CONVERSATION_MESSAGES

class ConversationService:
    VALID_ROLES = {"user", "assistant"}

    def __init__(self, max_messages: int = MAX_CONVERSATION_MESSAGES):
        if max_messages <= 0:
            raise ValueError("max_messages must be a positive integer.")
        self.max_messages = max_messages

    def create_conversation(self) -> List[Dict[str, str]]:
        """Create a new conversation with an empty message list."""
        return []

    def add_message(self, conversation: List[Dict[str, str]], role: str, content: str) -> None:
        """Add a message to the conversation."""
        if role not in self.VALID_ROLES:
            raise ValueError(f"Invalid role: {role}. Must be one of {self.VALID_ROLES}.")

        if not isinstance(content, str):
            raise ValueError("Message content must be a string.")
        content = content.strip()

        if not content:
            raise ValueError("Message content cannot be empty.")
        conversation.append({"role": role, "content": content})
        return conversation

    def trim_conversation_history(self, conversation: List[Dict[str, str]], max_messages: Optional[int] = None) -> List[Dict[str, str]]:
        """Trim the conversation history to the last `max_messages` messages."""
        if not isinstance(conversation, list):
            raise ValueError("Conversation must be a list of messages.")
        
        if max_messages <= 0:
            return []

        if len(conversation) <= max_messages:
            return conversation

        return conversation[-max_messages:]

    def get_recent_history(self, conversation: List[Dict[str, str]],) -> str:
        """Get a formatted string of the recent conversation history."""
        if conversation is None:
            return []

        return self.trim_conversation_history(
            conversation=conversation,
            max_messages=self.max_messages,
        )

    def format_conversation_history(self, conversation_history: List[Dict[str, str]],) -> str:
        """
        Convert conversation history into text
        suitable for the RAG prompt.
        """

        if not conversation_history:
            return ""

        formatted_messages = []

        for message in conversation_history:

            role = message.get("role")
            content = message.get("content")

            if not role or not content:
                continue

            if role == "user":
                speaker = "USER"

            elif role == "assistant":
                speaker = "ASSISTANT"

            else:
                continue

            formatted_messages.append(f"{speaker}: {content}")

        return "\n\n".join(formatted_messages)