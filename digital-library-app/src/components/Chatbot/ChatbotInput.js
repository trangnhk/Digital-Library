const ChatbotInput = ({value, onChange, onSend,onKeyDown,disabled}) => {

    return (
        <div className="chatbot-input-container">

            <input
                type="text"
                value={value}
                onChange={(event) => onChange(event.target.value)}
                onKeyDown={onKeyDown}
                placeholder="Type your question..."
                className="chatbot-input"
                disabled={disabled}
            />


            <button
                className="chatbot-send-button"
                onClick={onSend}
                disabled={disabled || !value.trim()}
            >
                {disabled ? "..." : "Send"}
            </button>

        </div>
    );
};

export default ChatbotInput;