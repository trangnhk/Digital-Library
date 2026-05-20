/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.pojo.Category;
import com.trangnhk.services.CategoryService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class ApiCategoriesController {
    
    @Autowired
    private CategoryService categoryService;
    
    // GET
    @GetMapping
    public ResponseEntity<?> getCategories(@RequestParam Map<String, String> params){
        
        return ResponseEntity.ok(this.categoryService.getActiveCategories(params));
    }
    
    // GET by cateId
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        CategoryResponseDTO category = this.categoryService.getActiveCategoryById(categoryId);

        if (category == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Không tìm thấy category"
            ));
        }

        return ResponseEntity.ok(category);
    }
    
}
