/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

/**
 *
 * @author Admin
 */
public class AdminStatisticsOverViewDTO {
    private Long totalUsers;
    private Long totalDocuments;
    private Long totalApprovedDocuments;
    private Long totalPendingLibrarians;
    private Long totalBorrows;
    private Long totalAccesses;

    public AdminStatisticsOverViewDTO() {
    }

    public AdminStatisticsOverViewDTO(Long totalUsers, Long totalDocuments, Long totalApprovedDocuments, Long totalPendingLibrarians, Long totalBorrows, Long totalAccesses) {
        this.totalUsers = totalUsers;
        this.totalDocuments = totalDocuments;
        this.totalApprovedDocuments = totalApprovedDocuments;
        this.totalPendingLibrarians = totalPendingLibrarians;
        this.totalBorrows = totalBorrows;
        this.totalAccesses = totalAccesses;
    }
    
    

    /**
     * @return the totalUsers
     */
    public Long getTotalUsers() {
        return totalUsers;
    }

    /**
     * @param totalUsers the totalUsers to set
     */
    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    /**
     * @return the totalDocuments
     */
    public Long getTotalDocuments() {
        return totalDocuments;
    }

    /**
     * @param totalDocuments the totalDocuments to set
     */
    public void setTotalDocuments(Long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    /**
     * @return the totalApprovedDocuments
     */
    public Long getTotalApprovedDocuments() {
        return totalApprovedDocuments;
    }

    /**
     * @param totalApprovedDocuments the totalApprovedDocuments to set
     */
    public void setTotalApprovedDocuments(Long totalApprovedDocuments) {
        this.totalApprovedDocuments = totalApprovedDocuments;
    }

    /**
     * @return the totalPendingLibrarians
     */
    public Long getTotalPendingLibrarians() {
        return totalPendingLibrarians;
    }

    /**
     * @param totalPendingLibrarians the totalPendingLibrarians to set
     */
    public void setTotalPendingLibrarians(Long totalPendingLibrarians) {
        this.totalPendingLibrarians = totalPendingLibrarians;
    }

    /**
     * @return the totalBorrows
     */
    public Long getTotalBorrows() {
        return totalBorrows;
    }

    /**
     * @param totalBorrows the totalBorrows to set
     */
    public void setTotalBorrows(Long totalBorrows) {
        this.totalBorrows = totalBorrows;
    }

    /**
     * @return the totalAccesses
     */
    public Long getTotalAccesses() {
        return totalAccesses;
    }

    /**
     * @param totalAccesses the totalAccesses to set
     */
    public void setTotalAccesses(Long totalAccesses) {
        this.totalAccesses = totalAccesses;
    }

}
