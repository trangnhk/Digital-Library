/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Admin
 */
public class AiChatResponseDTO {
    private String answer;

    @JsonProperty("referenced_book")
    private ReferencedBook referencedBook;

    @JsonProperty("conversation_history")
    private List<ConversationMessage> conversationHistory;

    @JsonProperty("conversation_state")
    private ConversationState conversationState;
    

    /**
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return the referencedBook
     */
    public ReferencedBook getReferencedBook() {
        return referencedBook;
    }

    /**
     * @param referencedBook the referencedBook to set
     */
    public void setReferencedBook(ReferencedBook referencedBook) {
        this.referencedBook = referencedBook;
    }

    /**
     * @return the conversationHistory
     */
    public List<ConversationMessage> getConversationHistory() {
        return conversationHistory;
    }

    /**
     * @param conversationHistory the conversationHistory to set
     */
    public void setConversationHistory(List<ConversationMessage> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

    /**
     * @return the conversationState
     */
    public ConversationState getConversationState() {
        return conversationState;
    }

    /**
     * @param conversationState the conversationState to set
     */
    public void setConversationState(ConversationState conversationState) {
        this.conversationState = conversationState;
    }
    
}
