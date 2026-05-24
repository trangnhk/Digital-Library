/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AccessResponseDTO;
import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.dto.DocumentContentResponseDTO;
import com.trangnhk.repositories.AccessHistoryRepository;
import com.trangnhk.repositories.BorrowHistoryRepository;
import com.trangnhk.repositories.DocumentFileRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.services.SecureDocumentService;
import com.trangnhk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */

@Service
@Transactional
public class SecureDocumentServiceImpl implements SecureDocumentService{
    private static final int ACCESS_SPAM_SECONDS = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private DocumentFileRepository docFileRepo;

    @Autowired
    private AccessHistoryRepository accessRepo;

    @Autowired
    private BorrowHistoryRepository borrowRepo;

    @Override
    public AccessResponseDTO recordAccess(String username, Long documentId, String ipAddress) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public DocumentContentResponseDTO getDocumentContent(String username, Long documentId, Long fileId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BorrowResponseDTO borrowDocument(String username, Long documentId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
