/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import java.util.List;

/**
 *
 * @author Admin
 */
public class NotificationPageResponseDTO {
    private List<NotificationResponseDTO> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private long unreadCount;

    public NotificationPageResponseDTO(List<NotificationResponseDTO> items, int page, int size, long totalItems, int totalPages, long unreadCount) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.unreadCount = unreadCount;
        
        if (size > 0){
            this.totalPages = (int) Math.ceil((double) totalItems / size);
        }
        else{
            this.totalPages = 0;
        }
        
    }
    
    

    /**
     * @return the items
     */
    public List<NotificationResponseDTO> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<NotificationResponseDTO> items) {
        this.items = items;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the totalItems
     */
    public long getTotalItems() {
        return totalItems;
    }

    /**
     * @param totalItems the totalItems to set
     */
    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * @return the totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages the totalPages to set
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return the unreadCount
     */
    public long getUnreadCount() {
        return unreadCount;
    }

    /**
     * @param unreadCount the unreadCount to set
     */
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
    
}
