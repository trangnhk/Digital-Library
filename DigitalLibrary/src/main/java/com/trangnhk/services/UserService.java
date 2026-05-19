/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.RegisterRequestDTO;
import com.trangnhk.dto.UpdateProfileRequestDTO;
import com.trangnhk.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface UserService extends UserDetailsService{
    User getUserByUsername(String username);
    User addUser(RegisterRequestDTO dto, MultipartFile avatar);
    boolean authenticate(String username, String password);
    User updateMyProfile(String usernmae, UpdateProfileRequestDTO dto);
}
