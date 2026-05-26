/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.enums.BorrowStatus;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface AdminStatisticRepository {
    Long countTotalUsers();
    Long countTotalDocuments();
    Long countTotalApprovedDocuments();
    Long countTotalPendingLibrarians();
    Long countTotalBorrows();
    Long countTotalAccesses();
    
    List<Object[]> countAccessByMonth(Date fromDate, Date toDate, Long categoryId);
    List<Object[]> countAccessByYear(Date fromDate, Date toDate, Long categoryId);
    List<Object[]> countBorrowsByMonth(Date fromDate, Date toDate, BorrowStatus status, String ownerUsername);
    List<Object[]> countBorrowsByYear(Date fromDate, Date toDate, BorrowStatus status, String ownerUsername);
}
