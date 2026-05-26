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
public class AdminStatisticResponseDTO {
    private String type;
    private String period;
    private String fromDate;
    private String toDate;
    private String status;
    private Long total;
    private Long categoryId;
    private List<AdminStatisticPointDTO> data;

    public AdminStatisticResponseDTO() {
    }

    public AdminStatisticResponseDTO(String type, String period, String fromDate, String toDate, String status, Long total, Long categoryId, List<AdminStatisticPointDTO> data) {
        this.type = type;
        this.period = period;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
        this.total = total;
        this.categoryId = categoryId;
        this.data = data;
    }
    
    

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the period
     */
    public String getPeriod() {
        return period;
    }

    /**
     * @param period the period to set
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * @return the fromDate
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the total
     */
    public Long getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Long total) {
        this.total = total;
    }

    /**
     * @return the data
     */
    public List<AdminStatisticPointDTO> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<AdminStatisticPointDTO> data) {
        this.data = data;
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
    
    
}
