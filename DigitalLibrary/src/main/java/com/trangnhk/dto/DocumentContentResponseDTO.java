/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.DocumentFile;

/**
 *
 * @author Admin
 */
public class DocumentContentResponseDTO {
    private Long documentId;
    private Long fileId;
    private String fileUrl;
    private String fileExtension;
    private Long fileSize;
    
    public static DocumentContentResponseDTO fromDocumentFile(DocumentFile file){
        DocumentContentResponseDTO dto = new DocumentContentResponseDTO();
        
        dto.setFileId(file.getId());
        dto.setFileUrl(file.getFileUrl());
        dto.setFileExtension(file.getFileExtension());
        dto.setFileSize(file.getFileSize());
        
        if (file.getDocument() != null){
            dto.setDocumentId(file.getDocument().getId());
        }
        
        return dto;
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
     * @return the fileId
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * @param fileUrl the fileUrl to set
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * @return the fileExtension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * @param fileExtension the fileExtension to set
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * @return the fileSize
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    
    
}
