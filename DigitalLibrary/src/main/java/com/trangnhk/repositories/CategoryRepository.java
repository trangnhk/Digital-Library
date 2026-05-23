/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Category;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface CategoryRepository {
    List<Category> getActiveCategories(Map<String, String> params);
    long countActiveCategories(Map<String, String> params);
    Category getActiveCategoryById(Long categoryId);
    
    List<Category> getCates();
    Category getCategoryById(Long categoryId);
    Boolean nameExisted(String name);
    void addOrUpdateCategory(Category c);
    boolean existsByNameAndIdNot(String name, Long categoryId);
    
    
}
