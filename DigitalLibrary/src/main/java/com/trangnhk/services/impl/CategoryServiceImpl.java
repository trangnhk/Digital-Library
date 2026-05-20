/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.pojo.Category;
import com.trangnhk.repositories.CategoryRepository;
import com.trangnhk.services.CategoryService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class CategoryServiceImpl implements CategoryService{
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public PageResponseDTO<CategoryResponseDTO> getActiveCategories(Map<String, String> params) {
        List<Category> categories = this.categoryRepository.getActiveCategories(params);
        
        long totalItems = this.categoryRepository.countActiveCategories(params);
        
        int page = this.getPage(params);
        int size = this.getSize(params);
        
        List<CategoryResponseDTO> items = categories.stream()
                                                    .map(CategoryResponseDTO::fromCategory)
                                                    .collect(Collectors.toList());
        
        return new PageResponseDTO<>(items, page, size, totalItems);
        
        
    }
    
    private int getPage(Map<String, String> params) {
        if (params == null) {
            return 1;
        }

        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));

            if (page < 1) {
                return 1;
            }

            return page;

        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private int getSize(Map<String, String> params) {
        if (params == null) {
            return 10;
        }

        try {
            int size = Integer.parseInt(params.getOrDefault("size", "10"));

            if (size < 1) {
                return 10;
            }

            if (size > 20) {
                return 20;
            }

            return size;

        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    @Override
    public CategoryResponseDTO getActiveCategoryById(Long categoryId) {
        Category category = this.categoryRepository.getActiveCategoryById(categoryId);

        if (category == null) {
            return null;
        }

        return CategoryResponseDTO.fromCategory(category);
    }
    
    
    
    
    
    
}
