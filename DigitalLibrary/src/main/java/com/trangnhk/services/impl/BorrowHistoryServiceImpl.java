/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.User;
import com.trangnhk.repositories.BorrowHistoryRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.BorrowHistoryService;
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
        
        return list.stream().map(BorrowResponseDTO::fromBorrowHistory).toList();
    }
    
}
