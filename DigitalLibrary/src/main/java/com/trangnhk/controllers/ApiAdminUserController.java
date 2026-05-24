/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.UpdateUserActiveRequestDTO;
import com.trangnhk.dto.UserResponseDTO;
import com.trangnhk.services.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
 * @author user
 */
@RestController
@RequestMapping("/api/secure/admin")
public class ApiAdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(this.userService.getUsers(params));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUserDetail(@PathVariable(value = "userId") Long userId) {

        UserResponseDTO response = this.userService.getUserDetail(userId);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/users/{userId}/active")
    public ResponseEntity<?> updateUserActive(@PathVariable("userId") Long userId, Principal principal, @Valid @RequestBody UpdateUserActiveRequestDTO dto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(this.buildValidationErrorResponse(bindingResult));
        }
        
        try{
            return ResponseEntity.ok(this.userService.updateUserActive(principal.getName(), userId, dto));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
    }

    @GetMapping("/librarians/pending")
    public ResponseEntity<List<UserResponseDTO>> getPendingLibrarians() {

        List<UserResponseDTO> response = this.userService.getPendingLibrarians();

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/librarians/{userId}/approve")
    public ResponseEntity<?> approveLibrarian(@PathVariable("userId") Long userId, Principal principal){
        try{
            return ResponseEntity.ok(this.userService.approveLibrarian(principal.getName(), userId));
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
        
    }
    
    @PatchMapping("/librarians/{userId}/reject")
    public ResponseEntity<?> rejectLibrarian(@PathVariable("userId") Long userId, Principal principal){
        try{
            return ResponseEntity.ok(this.userService.rejectLibrarian(principal.getName(), userId));
            
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
    
}
