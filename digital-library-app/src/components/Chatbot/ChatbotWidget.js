import { useState } from "react";
import ChatbotHeader from "./ChatbotHeader";
import ChatbotMessage from "./ChatbotMessage";
import ChatbotInput from "./ChatbotInput";

import "./ChatbotWidget.css";
import useChatbot from "./hooks/useChatbot";
import robotAvatar from "../../images/robot.png";


const ChatbotWidget = () => {

    const [isOpen, setIsOpen] = useState(false);


    const {
        messages,
        referenceBook,
        inputMessage,
        isLoading,
        error,

        setInputMessage,

        sendMessage,
        newChat
    } = useChatbot();


    // OPEN/CLOSE CHATBOT
    const handleToggleChatbot = () => {
        setIsOpen((previousState) => !previousState);
    };


    // SEND MESSAGE ON ENTER KEY
    const handleInputKeyDown = (event) => {

        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }

    };

    if (!isOpen) {

        return (
            <button className="chatbot-open-button" onClick={handleToggleChatbot}>
                <img src={robotAvatar} alt="Chatbot" />
            </button>
        );

    }

    return (
        <div className="chatbot-widget">
            <ChatbotHeader onNewChat={newChat} onClose={handleToggleChatbot}/>

            <div className="chatbot-messages">

                {messages.map((message, index) => (

                        <ChatbotMessage key={index}
                            message={message}
                            isLastMessage={index === messages.length - 1}
                            referencedBook={referenceBook}
                        />

                    )
                )}

                {isLoading && (<div className="chatbot-loading">AI is typing...</div>)}
                
                {error && (<div className="chatbot-error">{error}</div>)}

            </div>


            <ChatbotInput
                value={inputMessage}
                onChange={setInputMessage}
                onSend={sendMessage}
                onKeyDown={handleInputKeyDown}
                disabled={isLoading}
            />

        </div>
    );
};

export default ChatbotWidget;