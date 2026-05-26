/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.CategoryRequestDTO;
import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.dto.UpdateCategoryRequestDTO;
import com.trangnhk.pojo.Category;
import com.trangnhk.services.CategoryService;
import jakarta.validation.Valid;
import java.lang.module.ResolutionException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/api/secure/admin/categories")
@CrossOrigin
@Validated
public class ApiAdminCategoriesController {

    @Autowired
    private CategoryService categoryService;

    //Get
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return new ResponseEntity<>(this.categoryService.getCates(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDTO dto, BindingResult rs) {
        if (rs.hasErrors()) {
            return ResponseEntity.badRequest().body(rs.getAllErrors());
        }

        try {
            CategoryResponseDTO response = this.categoryService.createCategory(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException ex) {

            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable(value = "categoryId") Long categoryId,
            @Valid @RequestBody UpdateCategoryRequestDTO dto,
            BindingResult rs) {

        if (rs.hasErrors()) {
            return ResponseEntity.badRequest().body(rs.getAllErrors());
        }

        try {

            CategoryResponseDTO response
                    = this.categoryService.updateCategory(categoryId, dto);

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {

            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

}
