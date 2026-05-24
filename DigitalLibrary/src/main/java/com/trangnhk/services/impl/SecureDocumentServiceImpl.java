/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AccessResponseDTO;
import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.dto.DocumentContentResponseDTO;
import com.trangnhk.pojo.AccessHistory;
import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.DocumentFile;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.repositories.AccessHistoryRepository;
import com.trangnhk.repositories.BorrowHistoryRepository;
import com.trangnhk.repositories.DocumentFileRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.services.SecureDocumentService;
import com.trangnhk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        User u = this.getActiveUser(username);
        
        Document doc = this.getApprovedDocument(documentId);
        
        boolean spam = this.accessRepo.existRecentAccess(u.getId(), doc.getId(), ipAddress, ACCESS_SPAM_SECONDS);
        
        if (spam){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many access requests");
            
        }
        
        AccessHistory access = new AccessHistory();
        access.setUser(u);
        access.setDocument(doc);
        access.setIpAddress(ipAddress);
        
        AccessHistory savedAccess = this.accessRepo.add(access);
        
        Integer totalViews = doc.getTotalViews();
        if(totalViews == null){
            totalViews = 0;
        }
        
        doc.setTotalViews(totalViews + 1);
        
        this.docRepo.update(doc);
        
        return AccessResponseDTO.fromAccessHistory(savedAccess, doc.getTotalViews());

        
    }
    
    private User getActiveUser(String username){
        User u = this.userService.getUserByUsername(username);
        
        if(u == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        
        if (!Boolean.TRUE.equals(u.getActive())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is deactive");
        }
        
        return u;
        
    }
    
    private Document getApprovedDocument(Long documentId){
        Document doc = this.docRepo.getDocumentById(documentId);
        
        if (doc == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not foudn");
        }
        
        if (Boolean.FALSE.equals(doc.getApproved())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document is not approved");
        }
        
        return doc;
        
        
    }

    @Override
    public DocumentContentResponseDTO getDocumentContent(String username, Long documentId, Long fileId) {
        User u = this.getActiveUser(username);
        
        Document doc = this.getApprovedDocument(documentId);
        
        if (Boolean.TRUE.equals(doc.getPremium()) && !this.hasPaidDocument(u, doc)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment required for premium document");
        }
        
        DocumentFile file;
        
        if (fileId != null){
            file = this.docFileRepo.getFileByIdAndDocumnetId(fileId, documentId);
        }else{
            file = this.docFileRepo.getFirstFileByDocumentId(documentId);
        }
        
        if (file == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file not found");
        }
        
        return DocumentContentResponseDTO.fromDocumentFile(file);
        
        
    }
    
    private boolean hasPaidDocument(User u, Document doc){
        return false;
    }

    @Override
    public BorrowResponseDTO borrowDocument(String username, Long documentId) {
        User u = this.getActiveUser(username);
        Document doc = this.getApprovedDocument(documentId);
        
        if (this.isAdmin(u) || this.isLibrarian(u)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin and Librarian can't borrow document");
        }
        
        boolean alreadyBorrowing = this.borrowRepo.existOpenBorrow(u.getId(), doc.getId());
        
        if (alreadyBorrowing){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already borrowing this document");
        }
        
        BorrowHistory borrow = new BorrowHistory();
        borrow.setUser(u);
        borrow.setDocument(doc);
        borrow.setStatus(BorrowStatus.BORROWING);
        
        BorrowHistory savedBorrow = this.borrowRepo.add(borrow);
        
        return BorrowResponseDTO.fromBorrowHistory(savedBorrow);
                
        
        
    }
    
    private boolean isAdmin(User user) {
        return user != null && "ROLE_ADMIN".equals(user.getRole().toString());
    }

    private boolean isLibrarian(User user) {
        return user != null && "ROLE_LIBRARIAN".equals(user.getRole().toString());
    }
    
}
