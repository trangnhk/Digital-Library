/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.CreatePaymentRequestDTO;
import com.trangnhk.services.PaymentService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/secure/payments")
public class ApiPaymentController {
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<?> createPayment(Principal principal, @Valid @RequestBody CreatePaymentRequestDTO dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(this.buildValidationError(bindingResult));
        }

        try {
            return ResponseEntity.status(201).body(this.paymentService.createPayment(principal.getName(), dto));

        } catch (ResponseStatusException ex) {
            return this.buildErrorResponse(ex);
        }
        
        
    }
    
    @GetMapping("/{documentId}")
    public ResponseEntity<?> checkSuccessPaymentForDocument(Principal principal, @PathVariable("documentId") Long documentId){
        try{
            boolean hasPaid = this.paymentService.checkIsPaidDocument(principal.getName(), documentId);
            
            return ResponseEntity.ok(hasPaid);
            
        } catch (ResponseStatusException ex){
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

    private Map<String, Object> buildValidationError(BindingResult bindingResult) {
        Map<String, String> details = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Dữ liệu không hợp lệ");
        response.put("details", details);

        return response;
    }
}
