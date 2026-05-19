/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public class UpdateProfileRequestDTO {
    @Size(max = 100, message = "First Name mustn't be more than 100 letters")
    private String firstName;
    
    @Size(max = 100, message = "Last Name mustn't be more than 100 letters")
    private String lastName;
    
    @Email(message = "Invalid email")
    private String email;
    
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone must have 10 digits"
    )
    private String phone;
    
    private MultipartFile avatar;

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the avatar
     */
    public MultipartFile getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
    
    
    
    
}
