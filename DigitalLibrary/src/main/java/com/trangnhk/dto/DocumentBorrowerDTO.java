/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trangnhk.pojo.enums.BorrowStatus;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class DocumentBorrowerDTO {
    private Long borrowHistoryId;
    private Long documentId;
    private String documentTitle;
    private Long userId;
    private String username;
    private String fullname;
    private BorrowStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date borrowDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date returnDate;

    public DocumentBorrowerDTO() {
    }

    public DocumentBorrowerDTO(Long borrowHistoryId, Long documentId, String documentTitle, Long userId, String username, String fullname, BorrowStatus status, Date borrowDate, Date returnDate) {
        this.borrowHistoryId = borrowHistoryId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
        this.status = status != null ? status : null;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }
    
    

    /**
     * @return the borrowHistoryId
     */
    public Long getBorrowHistoryId() {
        return borrowHistoryId;
    }

    /**
     * @param borrowHistoryId the borrowHistoryId to set
     */
    public void setBorrowHistoryId(Long borrowHistoryId) {
        this.borrowHistoryId = borrowHistoryId;
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
     * @return the documentTitle
     */
    public String getDocumentTitle() {
        return documentTitle;
    }

    /**
     * @param documentTitle the documentTitle to set
     */
    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname the fullname to set
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return the status
     */
    public BorrowStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    /**
     * @return the borrowDate
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * @param borrowDate the borrowDate to set
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * @return the returnDate
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @param returnDate the returnDate to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    
    
}
