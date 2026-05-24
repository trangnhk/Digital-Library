/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.AccessHistory;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class AccessResponseDTO {
    private Long id;
    private Long documentId;
    private Long userId;
    private Date accessTime;
    private Integer totalViews;
    
    public static AccessResponseDTO fromAccessHistory(AccessHistory accessHistory, Integer totalViews){
        AccessResponseDTO dto = new AccessResponseDTO();
        
        dto.setId(accessHistory.getId());
        dto.setAccessTime(accessHistory.getAccessTime());
        dto.setTotalViews(totalViews);
        
        if (accessHistory.getDocument() != null){
            dto.setDocumentId(accessHistory.getDocument().getId());
        }
        
        if (accessHistory.getUser() != null){
            dto.setUserId(accessHistory.getUser().getId());
        }
        
        return dto;
    }
    

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return the accessTime
     */
    public Date getAccessTime() {
        return accessTime;
    }

    /**
     * @param accessTime the accessTime to set
     */
    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    /**
     * @return the totalViews
     */
    public Integer getTotalViews() {
        return totalViews;
    }

    /**
     * @param totalViews the totalViews to set
     */
    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }
    
    
    
}
