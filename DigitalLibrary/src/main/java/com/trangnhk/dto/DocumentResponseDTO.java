/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.enums.DocumentType;

/**
 *
 * @author Admin
 */
public class DocumentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String publisher;
    private Integer publishYear;
    private String thumbnail;
    private DocumentType documentType;
    private Boolean premium;
    private Double price;
    private Long categoryId;
    private String categoryName;
    private Integer totalViews;
    private Integer totalDownloads;
    private Double averageRating;
    
    public static DocumentResponseDTO fromDocument(Document document){
        DocumentResponseDTO dto = new DocumentResponseDTO();
        
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setAuthor(document.getAuthor());
        dto.setPublisher(document.getPublisher());
        dto.setPublishYear(document.getPublishYear());
        dto.setThumbnail(document.getThumbnail());
        dto.setDocumentType(document.getDocumentType());
        dto.setPremium(document.getPremium());
        dto.setPrice(document.getPrice());
        dto.setTotalViews(document.getTotalViews());
        dto.setTotalDownloads(document.getTotalDownloads());
        dto.setAverageRating(document.getAverageRating());
        
        if (document.getCategory() != null) {
            dto.setCategoryId(document.getCategory().getId());
            dto.setCategoryName(document.getCategory().getName());
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the documentType
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * @param documentType the documentType to set
     */
    public void setDocumentType(DocumentType documentType) {
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
    
    
}
