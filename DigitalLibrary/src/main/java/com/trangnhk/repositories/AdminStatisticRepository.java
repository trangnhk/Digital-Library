/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

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
}
