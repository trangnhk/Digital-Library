/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.User;
import java.util.List;
import java.util.Map;

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
    
    List<User> getUsers(Map<String, String> params);
    public User getUserById(Long userId);
    
    List<User> getPendingLibrarians();
}
