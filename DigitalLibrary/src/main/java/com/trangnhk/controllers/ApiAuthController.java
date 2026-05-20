/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.AuthRequestDTO;
import com.trangnhk.dto.ChangePasswordRequestDTO;
import com.trangnhk.dto.LoginRequestDTO;
import com.trangnhk.dto.RegisterRequestDTO;
import com.trangnhk.pojo.User;
import com.trangnhk.services.UserService;
import com.trangnhk.utils.JWTUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiAuthController {
    
    @Autowired
    private UserService userService;
    
    
    // REGISTER
    @PostMapping(path = "/auth/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @Valid @ModelAttribute RegisterRequestDTO dto,
            BindingResult rs,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ){
        // validation
        if (rs.hasErrors())
            return ResponseEntity.badRequest().body(rs.getAllErrors());
        
        User u = this.userService.addUser(dto, avatar);
        
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body("Register user success");
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        
    }
    
    // Login
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequestDTO dto,
            BindingResult rs
    ){
        // validation
        if (rs.hasErrors())
            return ResponseEntity.badRequest().body(rs.getAllErrors());
        
        boolean authenticated = this.userService.authenticate(dto.getUsername(), dto.getPassword());
        
        if (!authenticated)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        
        try{
            User u = this.userService.getUserByUsername(dto.getUsername());
            
            String token = JWTUtils.generateToken(u.getUsername(), u.getRole().name());
            
            AuthRequestDTO response = new AuthRequestDTO(token, u.getUsername(), u.getRole().name());
            
            return ResponseEntity.ok(response);
        
        } catch (Exception ex){
            return ResponseEntity.internalServerError().body("JWT generation failed");
        }
        
    }
    
    // Change password
    @PatchMapping("/secure/change-password")
    public ResponseEntity<?> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequestDTO dto
    ){
        this.userService.changePassword(principal.getName(), dto);
        
        return ResponseEntity.ok("Successfully change password");
    }
}
