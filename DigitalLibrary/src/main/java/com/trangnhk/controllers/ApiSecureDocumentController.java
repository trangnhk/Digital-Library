/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.services.SecureDocumentService;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */

@RestController
@RequestMapping("/api/secure/documents")
public class ApiSecureDocumentController {
    @Autowired
    private SecureDocumentService secureDocService;
    
    @PostMapping("/{documentId}/access")
    public ResponseEntity<?> recordAccess(@PathVariable("documentId") Long documentId, Principal principal, HttpServletRequest request){
        try{
            
            String ipAdress = this.getClientIp(request);
            
            return ResponseEntity.status(201).body(this.secureDocService.recordAccess(principal.getName(), documentId, ipAdress));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
    }
    
    @GetMapping("/{documentId}/content")
    public ResponseEntity<?> getDocumentContent(@PathVariable("documentId") Long documentId, @RequestParam(value = "fileId", required = false) Long fileId, Principal principal){
        try{
            return ResponseEntity.ok(this.secureDocService.getDocumentContent(principal.getName(), documentId, fileId));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
        
    }
    
    @PostMapping("/{documentId}/borrow")
    public ResponseEntity<?> borrowDocument(@PathVariable("documentId") Long documentId, Principal principal){
        try{
            return ResponseEntity.status(201).body(this.secureDocService.borrowDocument(principal.getName(), documentId));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
        
    }
    
    
    private String getClientIp(HttpServletRequest request){
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()){
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private ResponseEntity<?> buildErrorResponse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getStatusCode().toString(),
                "message", ex.getReason()
        ));
    }
    
}
