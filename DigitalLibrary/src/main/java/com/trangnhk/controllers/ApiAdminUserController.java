/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.UserResponseDTO;
import com.trangnhk.services.UserService;
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

    @GetMapping("/users/{userId")
    public ResponseEntity<UserResponseDTO> getUserDetail(@PathVariable(value = "userId") Long userId) {

        UserResponseDTO response = this.userService.getUserDetail(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/librarians/pending")
    public ResponseEntity<List<UserResponseDTO>> getPendingLibrarians() {

        List<UserResponseDTO> response = this.userService.getPendingLibrarians();

        return ResponseEntity.ok(response);
    }

}
