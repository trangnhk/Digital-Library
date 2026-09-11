
import robotAvatar from "../../images/robot.png";
import userAvatar from "../../images/user.png";
import ChatbotBookCard from "./ChatbotBookCard";

const ChatbotMessage = ({message, referencedBook, isLastMessage}) => {

    const isUser = message.role === "user";


    return (
        <div className={isUser ? "chatbot-message-row user" : "chatbot-message-row assistant"}>

            <div className="chatbot-message-avatar">
                {isUser ? <img src={userAvatar} alt="User Avatar" className="chatbot-avatar" /> : <img src={robotAvatar} alt="Chatbot Avatar" className="chatbot-avatar" />}
            </div>


            <div className="chatbot-message-content">

                <div className="chatbot-message-name">
                    {isUser ? "You" : "AI"}
                </div>


                <div className="chatbot-message-bubble">
                    {message.content}
                </div>


                {!isUser &&
                    isLastMessage &&
                    referencedBook && (
                        <ChatbotBookCard book={referencedBook}/>
                    )}

            </div>

        </div>
    );
};

export default ChatbotMessage;