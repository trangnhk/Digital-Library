/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.CategoryRequestDTO;
import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.UpdateCategoryRequestDTO;
import com.trangnhk.pojo.Category;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface CategoryService {
    PageResponseDTO<CategoryResponseDTO> getActiveCategories(Map<String, String> params);
    CategoryResponseDTO getActiveCategoryById(Long categoryId);
    
    List<CategoryResponseDTO> getCates();
    CategoryResponseDTO createCategory(CategoryRequestDTO dto);
    CategoryResponseDTO updateCategory(Long categoryId, UpdateCategoryRequestDTO dto);
}
