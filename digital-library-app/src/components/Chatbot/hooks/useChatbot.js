import { useState } from "react";
import Api, { endpoints } from "../../../configs/Apis";


const useChatbot = () => {

    const [messages, setMessages] = useState([]);
    const [referenceBook, setReferenceBook] = useState(null);
    const [conversationHistory, setConversationHistory] = useState([]);
    const [conversationState, setConversationState] = useState(null);
    const [inputMessage, setInputMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);


    // SEND MESSAGE
    const sendMessage = async () => {

        const message = inputMessage.trim();

        if (!message || isLoading) {
            return;
        }

        const userMessage = {
            role: "user",
            content: message
        };


        setMessages((previousMessages) => [
            ...previousMessages,
            userMessage
        ]);


        const request = {
            message: message,
            conversation_history: conversationHistory,
            conversation_state: conversationState,
            top_k: 5
        };

        // Clear the input field
        setInputMessage("");
        setIsLoading(true);
        setError(null);


        try {

            const response = await Api.post(endpoints.chatbotAi, request).then((res) => res.data);

            const assistantMessage = {
                role: "assistant",
                content: response.answer
            };

            setMessages((previousMessages) => [...previousMessages, assistantMessage]);
            setConversationHistory(response.conversation_history || []);
            setConversationState(response.conversation_state || null);
            setReferenceBook(response.referenced_book || null);

        } catch (error) {

            console.error("Chatbot API error:", error);
            setError("Unable to connect to the AI service.");

        } finally {
            setIsLoading(false);

        }
    };


    // NEW CHAT
    const newChat = () => {

        setMessages([]);

        setConversationHistory([]);

        setConversationState(null);

        setInputMessage("");

        setError(null);
    };


    return {
        messages,
        conversationHistory,
        conversationState,
        inputMessage,
        isLoading,
        error,
        referenceBook,
        setInputMessage,

        sendMessage,

        newChat
    };
};

export default useChatbot;