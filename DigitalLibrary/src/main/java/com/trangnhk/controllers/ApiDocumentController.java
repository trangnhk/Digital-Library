/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.services.DocumentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */

@RestController
@RequestMapping("/api/documents")
public class ApiDocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping
    public ResponseEntity<?> getDocuments(@RequestParam Map<String, String> params){
        String validationError = this.documentService.validatePublicDocumentParams(params);
        
        if (validationError != null){
            if (validationError.equals("CATEGORY_NOT_FOUND")) {
                return ResponseEntity.status(404).body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 404,
                        "error", "Not Found",
                        "message", "Can't found Category"
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 400,
                    "error", "Bad Request",
                    "message", validationError
            ));
        }
        
        return ResponseEntity.ok(this.documentService.getPublicDocuments(params));
        
    }
    
    @GetMapping("/{documentId}")
    public ResponseEntity<?> getDocumentById(@PathVariable("documentId") Long documentId) {
        DocumentResponseDTO document = this.documentService.getPublicDocumentById(documentId);

        if (document == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Document not found"
            ));
        }

        return ResponseEntity.ok(document);
    }
    
    @GetMapping("/{documentId}/files")
    public ResponseEntity<?> getDocumentFiles(@PathVariable("documentId") Long documentId){
        List<DocumentFileResponseDTO> files = this.documentService.getPublicDocumentFiles(documentId);
        
        if (files == null){
            return ResponseEntity.status(404).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", "Document File not found"
            ));
        }
        return ResponseEntity.ok(files);
    }
}
