/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class AdminDocumentActionResponseDTO {
    private Long documentId;
    private String title;
    private Boolean approved;
    private String message;
    private Long notificationId;
    private String notificationTitle;
    private String notificationContent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date actionDate;

    public AdminDocumentActionResponseDTO() {
    }

    public AdminDocumentActionResponseDTO(Long documentId, String title, Boolean approved, String message, Long notificationId, String notificationTitle, String notificationContent, Date actionDate) {
        this.documentId = documentId;
        this.title = title;
        this.approved = approved;
        this.message = message;
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.actionDate = actionDate;
    }

    /**
     * @return the documentId
     */
    public Long getDocumentId() {
        return documentId;
    }

    /**
     * @param documentId the documentId to set
     */
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the approved
     */
    public Boolean getApproved() {
        return approved;
    }

    /**
     * @param approved the approved to set
     */
    public void setApproved(Boolean approved) {
        this.approved = approved;
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
     * @return the notificationId
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * @param notificationId the notificationId to set
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * @return the notificationTitle
     */
    public String getNotificationTitle() {
        return notificationTitle;
    }

    /**
     * @param notificationTitle the notificationTitle to set
     */
    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    /**
     * @return the notificationContent
     */
    public String getNotificationContent() {
        return notificationContent;
    }

    /**
     * @param notificationContent the notificationContent to set
     */
    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    /**
     * @return the actionDate
     */
    public Date getActionDate() {
        return actionDate;
    }

    /**
     * @param actionDate the actionDate to set
     */
    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }
    
    
}
