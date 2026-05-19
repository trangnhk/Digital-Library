/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.User;

/**
 *
 * @author Admin
 */
public interface UserRepository {
    User getUserByUsername (String username);
    boolean existEmail(String email);
    boolean existPhone(String phone);
    
    User addUser(User u);
    User update(User u);
    boolean authenticate(String username, String password);
}
