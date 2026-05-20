/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface CategoryService {
    PageResponseDTO<CategoryResponseDTO> getActiveCategories(Map<String, String> params);
    CategoryResponseDTO getActiveCategoryById(Long categoryId);
}
