/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.UpdateProfileRequestDTO;
import com.trangnhk.pojo.User;
import com.trangnhk.services.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/secure/profile")
public class ApiProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = this.userService.getUserByUsername(principal.getName());

        return ResponseEntity.ok(user);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(Principal principal,
            @Valid @ModelAttribute UpdateProfileRequestDTO dto,
            BindingResult bindingResult) {
        
        if (dto.getAvatar() == null) {
            System.out.println("AVATAR = NULL");
        } else {
            System.out.println("AVATAR NAME = " + dto.getAvatar().getOriginalFilename());
            System.out.println("AVATAR SIZE = " + dto.getAvatar().getSize());
            System.out.println("AVATAR TYPE = " + dto.getAvatar().getContentType());
            System.out.println("AVATAR EMPTY = " + dto.getAvatar().isEmpty());
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        User updateUser = this.userService.updateMyProfile(principal.getName(), dto);

        return ResponseEntity.ok(updateUser);

    }
}
