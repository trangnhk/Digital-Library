/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.Document;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class AdminDocumentResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String documentType;
    private Boolean premium;
    private Double price;
    private Boolean approved;
    private Long categoryId;
    private String categoryName;
    private Long uploadedById;
    private String uploadedByUsername;
    private Integer totalViews;
    private Integer totalDownloads;
    private Double averageRating;
    private Date createdDate;
    
    public static AdminDocumentResponseDTO fromDocument(Document document) {
        AdminDocumentResponseDTO dto = new AdminDocumentResponseDTO();

        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setAuthor(document.getAuthor());
        dto.setPublisher(document.getPublisher());
        dto.setPublishYear(document.getPublishYear());

        if (document.getDocumentType() != null) {
            dto.setDocumentType(document.getDocumentType().toString());
        }

        dto.setPremium(document.getPremium());
        dto.setPrice(document.getPrice());
        dto.setApproved(document.getApproved());
        dto.setTotalViews(document.getTotalViews());
        dto.setTotalDownloads(document.getTotalDownloads());
        dto.setAverageRating(document.getAverageRating());
        dto.setCreatedDate(document.getCreatedDate());

        if (document.getCategory() != null) {
            dto.setCategoryId(document.getCategory().getId());
            dto.setCategoryName(document.getCategory().getName());
        }

        if (document.getUploadedBy() != null) {
            dto.setUploadedById(document.getUploadedBy().getId());
            dto.setUploadedByUsername(document.getUploadedBy().getUsername());
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
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the publishYear
     */
    public Integer getPublishYear() {
        return publishYear;
    }

    /**
     * @param publishYear the publishYear to set
     */
    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    /**
     * @return the documentType
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * @param documentType the documentType to set
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * @return the premium
     */
    public Boolean getPremium() {
        return premium;
    }

    /**
     * @param premium the premium to set
     */
    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
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
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the uploadedById
     */
    public Long getUploadedById() {
        return uploadedById;
    }

    /**
     * @param uploadedById the uploadedById to set
     */
    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }

    /**
     * @return the uploadedByUsername
     */
    public String getUploadedByUsername() {
        return uploadedByUsername;
    }

    /**
     * @param uploadedByUsername the uploadedByUsername to set
     */
    public void setUploadedByUsername(String uploadedByUsername) {
        this.uploadedByUsername = uploadedByUsername;
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

    /**
     * @return the totalDownloads
     */
    public Integer getTotalDownloads() {
        return totalDownloads;
    }

    /**
     * @param totalDownloads the totalDownloads to set
     */
    public void setTotalDownloads(Integer totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    /**
     * @return the averageRating
     */
    public Double getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating the averageRating to set
     */
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    
    
    
}
