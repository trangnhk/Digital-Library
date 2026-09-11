import robotAvatar from "../../images/robot.png";

const ChatbotHeader = ({onNewChat, onClose}) => {

    return (
        <div className="chatbot-header">

            <div className="chatbot-header-left">

                <div className="chatbot-avatar">
                    <img src={robotAvatar} alt="Chatbot Avatar" />
                </div>

                <div className="chatbot-title">

                    <div className="chatbot-title-main">Library AI Chatbot</div>
                    <div className="chatbot-status">Online</div>

                </div>

            </div>


            <div className="chatbot-header-actions">

                <button
                    className="chatbot-new-chat-button"
                    onClick={onNewChat}
                    title="Start a new conversation"
                >
                    + New Chat
                </button>


                <button
                    className="chatbot-close-button"
                    onClick={onClose}
                    title="Close chatbot"
                >
                    ×
                </button>

            </div>

        </div>
    );
};

export default ChatbotHeader;