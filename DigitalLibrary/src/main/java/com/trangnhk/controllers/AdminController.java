/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Admin
 */

@Controller
public class AdminController {
    @GetMapping("/admin/login")
    public String loginView(){
        return "admin/login";
    }
    
    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");

        return "admin/dashboard";
    }
    
    

}
