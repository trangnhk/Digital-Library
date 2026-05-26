/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

/**
 *
 * @author Admin
 */
public class AdminStatisticPointDTO {
    private String label;
    private Long total;

    public AdminStatisticPointDTO() {
    }

    public AdminStatisticPointDTO(String label, Long total) {
        this.label = label;
        this.total = total;
    }
    
    

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
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
    
    
    
}
