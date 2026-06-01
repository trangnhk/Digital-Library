/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.dto.DocumentBorrowerDTO;
import com.trangnhk.pojo.BorrowHistory;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface BorrowHistoryRepository {
    BorrowHistory add(BorrowHistory borrow);
    BorrowHistory update(BorrowHistory borrow);
    boolean existOpenBorrow(Long userId, Long documentId);
    BorrowHistory getOpenBorrow(Long userId, Long documentId);
    
    // LIBRARIAN
    List<DocumentBorrowerDTO> getBorrowerByDocumentId(Long documentId, Map<String, String> params);
    
    public List<BorrowHistory> getMyBorrows(Long userId, Map<String, String> params);
    
}
