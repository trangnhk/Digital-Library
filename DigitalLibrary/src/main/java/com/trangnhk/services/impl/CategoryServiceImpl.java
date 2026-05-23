/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.CategoryRequestDTO;
import com.trangnhk.dto.CategoryResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.UpdateCategoryRequestDTO;
import com.trangnhk.pojo.Category;
import com.trangnhk.repositories.CategoryRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.services.CategoryService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private DocumentRepository documentRepository;

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

    @Override
    public List<CategoryResponseDTO> getCates() {
        return this.categoryRepository.getCates().stream().map(CategoryResponseDTO::fromCategory).toList();
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        if (this.categoryRepository.nameExisted(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }

        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setActive(true);
        
        this.categoryRepository.addOrUpdateCategory(c);

        return CategoryResponseDTO.fromCategory(c);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long categoryId, UpdateCategoryRequestDTO dto) {
        Category category = categoryRepository.getCategoryById(categoryId);

        if (category == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category not found"
            );
        }

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            boolean exist = categoryRepository.existsByNameAndIdNot(dto.getName(), categoryId);

            if (exist) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Category name already exist"
                );
            }

            category.setName(dto.getName().trim());
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }

        if( dto.getActive() != null && dto.getActive() == false) {
            boolean hasActiveDocs = documentRepository.existsByCategoryIdAndActiveTrue(categoryId);

            if (hasActiveDocs) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Cannot deactivate category with active documents");
            }

            category.setActive(false);

        } else if (dto.getActive() != null) {
            category.setActive(true);
        }
        
        categoryRepository.addOrUpdateCategory(category);
        
        CategoryResponseDTO response =
                new CategoryResponseDTO();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.getActive());

        return response;
    }
}
