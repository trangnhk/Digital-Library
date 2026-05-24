/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trangnhk.dto.ChangePasswordRequestDTO;
import com.trangnhk.dto.RegisterRequestDTO;
import com.trangnhk.dto.UpdateProfileRequestDTO;
import com.trangnhk.dto.UserResponseDTO;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    public User updateMyProfile(String usernmae, UpdateProfileRequestDTO dto) {
        User currentU = this.getUserByUsername(usernmae);

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            currentU.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            currentU.setPhone(dto.getPhone());
        }

        if (dto.getFirstName() != null) {
            currentU.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            currentU.setLastName(dto.getLastName());
        }

        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            String contentType = dto.getAvatar().getContentType();

            if (!isValidImageType(contentType)) {
                throw new RuntimeException("Avatar must be an image as PNG, JPG or WEBP");
            }

            String avatarUrl = this.uploadAvatar(dto.getAvatar());
            currentU.setAvatar(avatarUrl);

        }

        return this.userRepo.update(currentU);

    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/webp"));
    }

    private String uploadAvatar(MultipartFile file) {
        try {
            Map uploadResult = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

            return uploadResult.get("secure_url").toString();
        } catch (IOException ex) {
            throw new RuntimeException("Upload avatar FAILED", ex);
        }
    }

    @Override
    public void changePassword(String username, ChangePasswordRequestDTO dto) {
        User currentU = this.userRepo.getUserByUsername(username);

        if (currentU == null) {
            throw new UsernameNotFoundException("Invalid user");
        }

        boolean oldPasswordMatches = this.passwordEncoder.matches(dto.getOldPassword(), currentU.getPassword());

        if (!oldPasswordMatches) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Old password isn't correct");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Old password isn't correct");
        }

        String encodeNewPassword = this.passwordEncoder.encode(dto.getNewPassword());

        currentU.setPassword(encodeNewPassword);

        this.userRepo.update(currentU);
    }

    @Override
    public List<UserResponseDTO> getUsers(Map<String, String> params) {
        List<User> users = this.userRepo.getUsers(params);

        return users.stream().map(UserResponseDTO::fromUser).toList();
    }

    @Override
    public UserResponseDTO getUserDetail(Long userId) {
        User user = this.userRepo.getUserById(userId);

        if (user == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        return UserResponseDTO.fromUser(user);
    }

    @Override
    public List<UserResponseDTO> getPendingLibrarians() {
        List<User> users = this.userRepo.getPendingLibrarians();

        return users.stream().map(UserResponseDTO::fromUser).toList();
    }
}
