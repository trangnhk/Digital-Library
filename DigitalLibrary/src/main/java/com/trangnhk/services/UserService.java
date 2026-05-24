/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.ChangePasswordRequestDTO;
import com.trangnhk.dto.RegisterRequestDTO;
import com.trangnhk.dto.UpdateProfileRequestDTO;
import com.trangnhk.dto.UserResponseDTO;
import com.trangnhk.pojo.User;
import java.util.List;
import java.util.Map;
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
    void changePassword(String username, ChangePasswordRequestDTO dto);
    
    List<UserResponseDTO> getUsers(Map<String, String> params);
    public UserResponseDTO getUserDetail(Long userId);
    
    List<UserResponseDTO> getPendingLibrarians();
}
