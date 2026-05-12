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
    @NamedQuery(name = "DocumentFile.findAll", query = "SELECT d FROM DocumentFile d"),
    @NamedQuery(name = "DocumentFile.findById", query = "SELECT d FROM DocumentFile d WHERE d.id = :id"),
    @NamedQuery(name = "DocumentFile.findByUploadedDate", query = "SELECT d FROM DocumentFile d WHERE d.uploadedDate = :uploadedDate")})
class DocumentFile implements Serializable{
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
}
