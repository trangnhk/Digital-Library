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
public class AiChatRequestDTO {
    private String message;
    
    @JsonProperty("conversation_history")
    private List<ConversationMessage> conversationHistory;
    
    @JsonProperty("conversation_state")
    private ConversationState conversationState;
    
    @JsonProperty("top_k")
    private Integer topK;

    public AiChatRequestDTO() {
    }
    
    

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
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

    /**
     * @return the topK
     */
    public Integer getTopK() {
        return topK;
    }

    /**
     * @param topK the topK to set
     */
    public void setTopK(Integer topK) {
        this.topK = topK;
    }
    
}
