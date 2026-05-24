/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.BorrowHistory;

/**
 *
 * @author Admin
 */
public interface BorrowHistoryRepository {
    BorrowHistory add(BorrowHistory borrow);
    boolean existOpenBorrow(Long userId, Long documentId);
}
