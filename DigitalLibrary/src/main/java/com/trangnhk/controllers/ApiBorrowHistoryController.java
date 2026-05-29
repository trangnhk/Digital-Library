/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.services.BorrowHistoryService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/api/secure/borrows")
public class ApiBorrowHistoryController {
    
    @Autowired
    private BorrowHistoryService borrowHistoryService;
    
    @GetMapping("/me")
    public ResponseEntity<?> getMyBorrows(@RequestParam Map<String, String> params, Principal principal){
        try{
            List<BorrowResponseDTO> borrows = this.borrowHistoryService.getMyBorrows(principal.getName(), params);
            
            return ResponseEntity.ok(borrows);
            
        } catch(ResponseStatusException ex){
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }
}
