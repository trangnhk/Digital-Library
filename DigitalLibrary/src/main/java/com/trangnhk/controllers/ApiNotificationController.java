/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.services.NotificationService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/secure/notifications")
public class ApiNotificationController {

    @Autowired
    private NotificationService notiService;

    @GetMapping
    public ResponseEntity<?> getMyNotifications(Principal principal, @RequestParam Map<String, String> params) {
        String validationError = this.notiService.validateNotificationParams(params);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 400,
                    "error", "Bad Request",
                    "message", validationError
            ));
        }

        try {
            return ResponseEntity.ok(this.notiService.getMyNotifications(principal.getName(), params));

        } catch (ResponseStatusException ex) {
            return this.buildErrorResponse(ex);
        }
    }
    
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> readNotificationById(@PathVariable("notificationId") Long notificationId, Principal principal){
        
        try{
            return ResponseEntity.ok(this.notiService.readNotification(principal.getName(), notificationId));
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
}
