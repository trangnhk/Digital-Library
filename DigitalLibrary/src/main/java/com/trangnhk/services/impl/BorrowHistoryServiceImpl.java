/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.repositories.BorrowHistoryRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.BorrowHistoryService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author user
 */
@Service
@Transactional
public class BorrowHistoryServiceImpl implements BorrowHistoryService{

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private BorrowHistoryRepository borrowHistoryRepo;
    
    @Override
    public List<BorrowResponseDTO> getMyBorrows(String username, Map<String, String> params) {
        User user = this.userRepo.getUserByUsername(username);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        List<BorrowHistory> list= this.borrowHistoryRepo.getMyBorrows(user.getId(), params);
        
        Date now = new Date();
        for(BorrowHistory borrow: list){
            if (borrow.getDueDate()!= null && now.after(borrow.getDueDate()) && BorrowStatus.BORROWING.equals(borrow.getStatus()) && Boolean.FALSE.equals(borrow.getDocument().getDeleted())){
                borrow.setStatus(BorrowStatus.EXPIRED);
                this.borrowHistoryRepo.update(borrow);
            }
        }
        
        return list.stream().map(BorrowResponseDTO::fromBorrowHistory).toList();
    }

    @Override
    public BorrowResponseDTO returnBorrow(String username, Long borrowId) {
        User currentU = this.userRepo.getUserByUsername(username);
        
        if (currentU == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        
        BorrowHistory borrow = this.borrowHistoryRepo.getBorrowById(currentU.getId(), borrowId);
            
        if (borrow == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found borrow history");
        }
        
        if (borrow.getUser().getId() != currentU.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to return this document borrowing");
        }
        
        if (BorrowStatus.RETURNED.equals(borrow.getStatus())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You have already RETURNED this on time");
        }
        
        if (BorrowStatus.EXPIRED.equals(borrow.getStatus())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "This document borrowing period has expired");
        }
        
        
        Date now = new Date();
        
        if (now.after(borrow.getDueDate())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This document borrowing has been expired, You can't return");
        }
        
        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnDate(now);
        
        this.borrowHistoryRepo.update(borrow);
        
        return BorrowResponseDTO.fromBorrowHistory(borrow);
        
    }
    
}
