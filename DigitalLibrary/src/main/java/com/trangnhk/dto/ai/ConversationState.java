/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Admin
 */

public class ConversationState {
    @JsonProperty("last_referenced_book")
    private ReferencedBook lastReferencedBook;

    public ConversationState() {
    }
    
    

    /**
     * @return the lastReferencedBook
     */
    public ReferencedBook getLastReferencedBook() {
        return lastReferencedBook;
    }

    /**
     * @param lastReferencedBook the lastReferencedBook to set
     */
    public void setLastReferencedBook(ReferencedBook lastReferencedBook) {
        this.lastReferencedBook = lastReferencedBook;
    }
    
}
