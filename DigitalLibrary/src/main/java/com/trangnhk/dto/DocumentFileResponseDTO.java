/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.DocumentFile;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class DocumentFileResponseDTO {
    private Long id;
    private String fileExtension;
    private Long fileSize;
    private Date uploadedDate;
    private Boolean active;
    private String accessUrl; // Muốn mở/tải file A thì phải gọi API secure nào?

    public static DocumentFileResponseDTO fromDocumentFile(DocumentFile file) {
        DocumentFileResponseDTO dto = new DocumentFileResponseDTO();

        dto.setId(file.getId());
        dto.setFileExtension(file.getFileExtension());
        dto.setFileSize(file.getFileSize());
        dto.setUploadedDate(file.getUploadedDate());
        dto.setActive(file.getActive());

        if (file.getDocument() != null) {
            dto.setAccessUrl("/api/secure/documents/"
                    + file.getDocument().getId()
                    + "/content?fileId="
                    + file.getId());
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

    /**
     * @return the uploadedDate
     */
    public Date getUploadedDate() {
        return uploadedDate;
    }

    /**
     * @param uploadedDate the uploadedDate to set
     */
    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    /**
     * @return the accessUrl
     */
    public String getAccessUrl() {
        return accessUrl;
    }

    /**
     * @param accessUrl the accessUrl to set
     */
    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    
}
