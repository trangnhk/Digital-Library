/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.services.SecureDocumentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */

@Controller
public class AdminController {
    @Autowired
    private SecureDocumentService docService;
    
    @GetMapping("/admin/login")
    public String loginView(){
        return "admin/login";
    }
    
    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");

        return "admin/dashboard";
    }
    
    @GetMapping("/admin/documents")
    public String documentsView(@RequestParam Map<String, String> params, Model model){
        PageResponseDTO documentsPage = this.docService.getAdminDocuments(params);

        model.addAttribute("title", "Documents Management");
        model.addAttribute("documentsPage", documentsPage);
        model.addAttribute("documents", documentsPage.getItems());

        model.addAttribute("keyword", params.getOrDefault("keyword", ""));
        model.addAttribute("approved", params.getOrDefault("approved", ""));
        model.addAttribute("categoryId", params.getOrDefault("categoryId", ""));
        model.addAttribute("uploadBy", params.getOrDefault("uploadBy", ""));
        model.addAttribute("sort", params.getOrDefault("sort", "newest"));
        model.addAttribute("page", params.getOrDefault("page", "1"));
        model.addAttribute("size", params.getOrDefault("size", "10"));

        model.addAttribute("content", "~{admin/documents :: documentsContent}");

        return "admin/documents";
    }

}
