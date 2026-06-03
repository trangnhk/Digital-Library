/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.enums.DocumentType;

/**
 *
 * @author user
 */
public class CompareDocumentDTO {
    private Long id;
    private String title;
    private String author;
    private Integer publishYear;
    private Integer totalViews;
    private Double averageRating;
    private String description;
    private Integer totalDowloads;
    private DocumentType documentType;
    private String thumbnail;
    

    public static CompareDocumentDTO fromDocument(Document d){
        CompareDocumentDTO dto = new CompareDocumentDTO();
        
        dto.setId(d.getId());
        dto.setTitle(d.getTitle());
        dto.setPublishYear(d.getPublishYear());
        dto.setDescription(d.getDescription());
        dto.setTotalViews(d.getTotalViews());
        dto.setAverageRating(d.getAverageRating());
        dto.setAuthor(d.getAuthor());
        dto.setTotalDowloads(d.getTotalDownloads());
        dto.setDocumentType(d.getDocumentType());
        dto.setThumbnail(d.getThumbnail());

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
     * @return the totalDowloads
     */
    public Integer getTotalDowloads() {
        return totalDowloads;
    }

    /**
     * @param totalDowloads the totalDowloads to set
     */
    public void setTotalDowloads(Integer totalDowloads) {
        this.totalDowloads = totalDowloads;
    }

    /**
     * @param documentType the documentType to set
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
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
