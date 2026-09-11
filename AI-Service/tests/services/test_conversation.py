from app.services.conversation_service import ConversationService


def main():

    service = ConversationService()

    # Create conversation
    conversation = (service.create_conversation())

    print("Initial:")
    print(conversation)

    # Add messages
    service.add_message(
        conversation,
        "user",
        "Tell me about Dracula.",
    )

    service.add_message(
        conversation,
        "assistant",
        "Dracula is a Gothic horror novel.",
    )

    service.add_message(
        conversation,
        "user",
        "Who wrote it?",
    )

    print("\nConversation:")
    print(conversation)

    # Get recent 
    recent_history = (service.get_recent_history(conversation))

    print("\nRecent history:")
    print(recent_history)

    # Format history
    history_text = (service.format_conversation_history(recent_history))

    print("\nFormatted history:")
    print(history_text)


if __name__ == "__main__":
    main()