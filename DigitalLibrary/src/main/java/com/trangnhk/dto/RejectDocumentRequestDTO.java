/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

/**
 *
 * @author Admin
 */
public class RejectDocumentRequestDTO {
    private String reason;
    
    public RejectDocumentRequestDTO(){
        
    }
    
    public RejectDocumentRequestDTO(String reason){
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
