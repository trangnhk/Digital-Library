/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.AccessResponseDTO;
import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.dto.DocumentContentResponseDTO;

/**
 *
 * @author Admin
 */
public interface SecureDocumentService {
    AccessResponseDTO recordAccess(String username, Long documentId, String ipAddress);
    DocumentContentResponseDTO getDocumentContent(String username, Long documentId, Long fileId);
    BorrowResponseDTO borrowDocument(String username, Long documentId);
}
