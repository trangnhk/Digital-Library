/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.Bookmark;
import java.util.Date;

/**
 *
 * @author user
 */
public class BookmarkResponseDTO {
    private Long id;
    private Date createDate;
    private Long documentId;
    private Long userId;
    private String thumbnail;

    public static BookmarkResponseDTO fromBookmark(Bookmark bookmark){
        BookmarkResponseDTO dto = new BookmarkResponseDTO();
        
        dto.setId(bookmark.getId());
        dto.setDocumentId(bookmark.getDocument().getId());
        dto.setCreateDate(bookmark.getCreatedDate());
        dto.setUserId(bookmark.getUser().getId());
        dto.setThumbnail(bookmark.getDocument().getThumbnail());
        
        
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
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
     * @return the thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
