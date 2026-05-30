/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

/**
 *
 * @author Admin
 */

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "document_files")
@NamedQueries({
    @NamedQuery(name = "DocumentFile.findAllByDocumentId", query = "SELECT d FROM DocumentFile d WHERE d.document.id = :documentId ORDER BY d.uploadedDate DESC"),
    @NamedQuery(name = "DocumentFile.findById", query = "SELECT d FROM DocumentFile d WHERE d.id = :id"),
    @NamedQuery(name = "DocumentFile.findByDocumentId",
                query = "SELECT d FROM DocumentFile d WHERE d.document.id = :documentId AND (d.active = true OR d.active IS NULL) ORDER BY d.uploadedDate DESC"),
    @NamedQuery(name = "DocumentFile.findByUploadedDate", query = "SELECT d FROM DocumentFile d WHERE d.uploadedDate = :uploadedDate")})
public class DocumentFile implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_extension")
    private String fileExtension;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "uploaded_date")
    private Date uploadedDate = new Date();
    
    @Column(name = "active")
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

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
     * @return the publicId
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * @param publicId the publicId to set
     */
    public void setPublicId(String publicId) {
        this.publicId = publicId;
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
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;
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
    
    public boolean isMatchingDocumentType(){
        if (this.document == null || this.document.getDocumentType() == null){
            return false;
        }
        
        if (this.fileExtension == null || this.fileExtension.trim().isEmpty()){
            return false;
        }
        
        String ext = this.fileExtension.trim().toLowerCase();
        
        switch (this.document.getDocumentType()){
            case PDF:
                return ext.equals("pdf");
            
            case DOCX:
                return ext.equals("docx");
                
            case EPUB:
                return ext.equals("epub");

            case VIDEO:
                return ext.equals("mp4");
                
            case AUDIO:
                return ext.equals("mp3") || ext.equals("wav");
                
            default:
                return false;
        }
        
    }
    
    public void refreshActiveByDocumentType(){
        this.active = this.isMatchingDocumentType();
    }
}
