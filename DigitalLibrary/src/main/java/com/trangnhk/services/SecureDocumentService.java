/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.AccessResponseDTO;
import com.trangnhk.dto.AdminDocumentActionResponseDTO;
import com.trangnhk.dto.AdminDocumentResponseDTO;
import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.dto.DocumentBorrowerDTO;
import com.trangnhk.dto.DocumentContentResponseDTO;
import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.RejectDocumentRequestDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface SecureDocumentService {
    AccessResponseDTO recordAccess(String username, Long documentId, String ipAddress);
    DocumentContentResponseDTO getDocumentContent(String username, Long documentId, Long fileId);
    BorrowResponseDTO borrowDocument(String username, Long documentId);
    
    //LIBRARIAN
    List<DocumentBorrowerDTO> getDocumentBorrowers(String username, Long documentId, Map<String, String> params);
    
    //ADMIN
    PageResponseDTO<AdminDocumentResponseDTO> getAdminDocuments(Map<String, String> params);
    String validateAdminDocumentParams(Map<String, String> params);
    AdminDocumentActionResponseDTO approveDocument(Long documentId);
    AdminDocumentActionResponseDTO rejectDocument(Long documentId, RejectDocumentRequestDTO dto);
}
