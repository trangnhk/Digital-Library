/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trangnhk.dto.RegisterRequestDTO;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service("userDetailService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public User addUser(RegisterRequestDTO dto, MultipartFile avatar) {
        User u = new User();
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setUsername(dto.getUsername());
        u.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        u.setActive(true);

        switch (dto.getRegisterType()) {
            case "STUDENT":
                u.setRole(UserRole.ROLE_STUDENT);
                break;

            case "LECTURER":
                u.setRole(UserRole.ROLE_LECTURER);
                break;

            case "LIBRARIAN":
                u.setRole(UserRole.ROLE_LIBRARIAN);
                break;

            default:
                throw new RuntimeException("Invalid register type");
        }

        if (avatar != null && !avatar.isEmpty()) {
            try {

                Map res = this.cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

                u.setAvatar(res.get("secure_url").toString());

            } catch (IOException ex) {
                throw new RuntimeException("Avatar upload failed");
            }
        }
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) throws UsernameNotFoundException {
        return this.userRepo.authenticate(username, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("LOGIN USERNAME = " + username);
        User u = this.getUserByUsername(username);

        if (u == null) {
            System.out.println("USER NOT FOUND");
            throw new UsernameNotFoundException("Invalid username");
        }

        if (!u.getActive()) {
            throw new UsernameNotFoundException("Account is disabled");
        }

        if (u.getRole() == UserRole.ROLE_LIBRARIAN && !u.getLibrarianVerified()) {
            throw new UsernameNotFoundException("Librarian account not approved yet");
        }
        
        System.out.println("DB USER = " + u.getUsername());
        System.out.println("DB ROLE = " + u.getRole());
        System.out.println("DB PASSWORD = " + u.getPassword());

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole().name()));

        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }
    
}
