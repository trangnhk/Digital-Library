/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Admin
 */
public class ReferencedBook {
    @JsonProperty("book_id")
    private Long bookId;
    
    private String title;
    
    @JsonProperty("thumbnail")
    private String thumbnail;

    public ReferencedBook() {
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
     * @return the bookId
     */
    public Long getBookId() {
        return bookId;
    }

    /**
     * @param bookId the bookId to set
     */
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /**
     * @return the thumbNail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbNail the thumbNail to set
     */
    public void setThumbnail(String thumbNail) {
        this.thumbnail = thumbNail;
    }
}
