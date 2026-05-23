/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.CreateLibrarianDocumentRequestDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.dto.UpdateLibrarianDocumentRequestDTO;
import com.trangnhk.services.DocumentService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/secure/librarian/documents")
public class ApiLibrarianDocumentController {

    @Autowired
    private DocumentService docService;

    @GetMapping
    public ResponseEntity<?> getMyDocuments(Principal principal, @RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok(this.docService.getManagedDocuments(principal.getName(), params));

        } catch (ResponseStatusException ex) {
            return this.buildErrorResponse(ex);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDocument(Principal principal, @Valid @ModelAttribute CreateLibrarianDocumentRequestDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.buildValidationErrorResponse(bindingResult));
        }

        try {
            return ResponseEntity.status(201).body(this.docService.createLibrarianDocument(principal.getName(), dto));

        } catch (ResponseStatusException ex) {
            return this.buildErrorResponse(ex);
        }
    }

    private ResponseEntity<?> buildErrorResponse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getStatusCode().toString(),
                "message", ex.getReason()
        ));
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
    
    @GetMapping("/{documentId}")
    public ResponseEntity<?> getDocumentDetail(@PathVariable("documentId") Long documentId, Authentication authenication){
        try{
            DocumentResponseDTO dto = this.docService.getDocumentDetail(documentId, authenication.getName());
            
            return ResponseEntity.ok(dto);
        } catch(ResponseStatusException ex){
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @PatchMapping(value = "/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@PathVariable("documentId") Long documentId, Principal principal, @Valid @ModelAttribute UpdateLibrarianDocumentRequestDTO dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(this.buildValidationErrorResponse(bindingResult));
        }
        
        try{
            return ResponseEntity.ok(this.docService.updateLibrarianDocument(principal.getName(), documentId, dto));
            
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
    }
    
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable("documentId") Long documentId, Principal principal){
        try{
            this.docService.deleteLibrarianDocument(principal.getName(), documentId);
            
            return ResponseEntity.noContent().build();
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
    }
    
}
