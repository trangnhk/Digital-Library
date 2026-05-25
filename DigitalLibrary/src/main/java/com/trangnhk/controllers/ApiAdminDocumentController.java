/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.AdminDocumentActionResponseDTO;
import com.trangnhk.dto.RejectDocumentRequestDTO;
import com.trangnhk.services.SecureDocumentService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */

@RestController
@RequestMapping("/api/secure/admin/documents")
public class ApiAdminDocumentController {
    @Autowired
    private SecureDocumentService docService;
    
    @GetMapping
    public ResponseEntity<?> getAdminDocuments(@RequestParam Map<String, String> params, Principal principal){
        try {
            return ResponseEntity.ok(this.docService.getAdminDocuments(params));

        } catch (ResponseStatusException ex) {
            return this.buildErrorResponse(ex);
        }
    }
    
    @PatchMapping("/{documentId}/approve")
    public ResponseEntity<?> approveDocument(@PathVariable("documentId") Long documentId){
//        if (bindingResult.hasErrors()){
//            return ResponseEntity.badRequest().body(this.buildValidationErrorResponse(bindingResult));
//        }
        
        try{
            return ResponseEntity.ok(this.docService.approveDocument(documentId));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
    }
    
    @PatchMapping("/{documentId}/reject")
    public ResponseEntity<?> rejectDocument(@PathVariable("documentId") Long documentId, @RequestBody(required = false) RejectDocumentRequestDTO dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(this.buildValidationErrorResponse(bindingResult));
        }
        
        try{
            return ResponseEntity.ok(this.docService.rejectDocument(documentId, dto));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
        
    }
    
    private Map<String, Object> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> details = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Invalid Data");
        response.put("details", details);

        return response;
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
