/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

/**
 *
 * @author Admin
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trangnhk.pojo.enums.DocumentType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "documents")
@JsonIgnoreProperties({
    "reviews",
    "payments",
    "borrowHistories",
    "accessHistories",
    "bookmarks"
})
@NamedQueries({
    @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d"),
    @NamedQuery(name = "Document.findById", query = "SELECT d FROM Document d WHERE d.id = :id"),
    @NamedQuery(name = "Document.findByTitle", query = "SELECT d FROM Document d WHERE d.title = :title"),
    @NamedQuery(name = "Document.findByAuthor", query = "SELECT d FROM Document d WHERE d.author = :author"),
    @NamedQuery(name = "Document.findByCreatedDate", query = "SELECT d FROM Document d WHERE d.createdDate = :createdDate")})
public class Document implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_THUMBNAIL_URL =
            "https://res.cloudinary.com/dxfbpkmen/image/upload/v1762312884/cld-sample-3.jpg";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;
    
    @Column(nullable = false, length = 500)
    @Basic(optional = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "author")
    @Basic(optional = false)
    private String author;
    
    @Column(name = "publisher")
    @Basic(optional = false)
    private String publisher;

    @Column(name = "publish_year")
    @Basic(optional = false)
    private Integer publishYear;

    @Column(name = "thumbnail", length = 500)
    private String thumbnail = DEFAULT_THUMBNAIL_URL;

    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private DocumentType documentType;
    
    @Column(name = "premium")
    @Basic(optional = false)
    private Boolean premium = false;

    @Column(name = "price")
    @Basic(optional = false)
    private Double price = 0.0;
    
    @Column(name = "approved")
    private Boolean approved = false;
    
    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "total_views")
    private Integer totalViews = 0;

    @Column(name = "total_downloads")
    private Integer totalDownloads = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @Basic(optional = false)
    private Date createdDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate = new Date();
    
    // RELATIONSHIPD
    
    @ManyToOne
    @JoinColumn(name = "upload_by")
    private User uploadedBy;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "document")
    private Set<DocumentFile> files;

    @OneToMany(mappedBy = "document")
    private Set<Review> reviews;

    @OneToMany(mappedBy = "document")
    private Set<Payment> payments;

    @OneToMany(mappedBy = "document")
    private Set<BorrowHistory> borrowHistories;

    @OneToMany(mappedBy = "document")
    private Set<AccessHistory> accessHistories;

    @OneToMany(mappedBy = "document")
    private Set<Bookmark> bookmarks;

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

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the uploadedBy
     */
    public User getUploadedBy() {
        return uploadedBy;
    }

    /**
     * @param uploadedBy the uploadedBy to set
     */
    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return the files
     */
    public Set<DocumentFile> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(Set<DocumentFile> files) {
        this.files = files;
    }

    /**
     * @return the reviews
     */
    public Set<Review> getReviews() {
        return reviews;
    }

    /**
     * @param reviews the reviews to set
     */
    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * @return the payments
     */
    public Set<Payment> getPayments() {
        return payments;
    }

    /**
     * @param payments the payments to set
     */
    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    /**
     * @return the borrowHistories
     */
    public Set<BorrowHistory> getBorrowHistories() {
        return borrowHistories;
    }

    /**
     * @param borrowHistories the borrowHistories to set
     */
    public void setBorrowHistories(Set<BorrowHistory> borrowHistories) {
        this.borrowHistories = borrowHistories;
    }

    /**
     * @return the accessHistories
     */
    public Set<AccessHistory> getAccessHistories() {
        return accessHistories;
    }

    /**
     * @param accessHistories the accessHistories to set
     */
    public void setAccessHistories(Set<AccessHistory> accessHistories) {
        this.accessHistories = accessHistories;
    }

    /**
     * @return the bookmarks
     */
    public Set<Bookmark> getBookmarks() {
        return bookmarks;
    }

    /**
     * @param bookmarks the bookmarks to set
     */
    public void setBookmarks(Set<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
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
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    
    
    
}
