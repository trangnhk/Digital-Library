/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.CreateLibrarianDocumentRequestDTO;
import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.dto.LibrarianDocumentResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface DocumentService {
    PageResponseDTO<DocumentResponseDTO> getPublicDocuments(Map<String, String> params);
    DocumentResponseDTO getPublicDocumentById(Long documentId);
    String validatePublicDocumentParams(Map<String, String> params);
    List<DocumentFileResponseDTO> getPublicDocumentFiles(Long documentId);
    
    PageResponseDTO<LibrarianDocumentResponseDTO> getManagedDocuments(String username, Map<String, String> params);
    LibrarianDocumentResponseDTO createLibrarianDocument(String username, CreateLibrarianDocumentRequestDTO dto);
}
