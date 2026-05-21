/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.DocumentFile;
import com.trangnhk.repositories.CategoryRepository;
import com.trangnhk.repositories.DocumentFileRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.services.DocumentService;
import com.trangnhk.utils.DocumentSorts;
import com.trangnhk.utils.SortUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class DocumentServiceImpl implements DocumentService{
    
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of("PDF", "DOCX", "EPUB", "VIDEO", "AUDIO");
    
    @Autowired
    private DocumentRepository documentRepo;
    
    @Autowired
    private CategoryRepository categoryRepo;
    
    @Autowired
    private DocumentFileRepository documentFileRepo;
    
    
    @Override
    public PageResponseDTO<DocumentResponseDTO> getPublicDocuments(Map<String, String> params) {
        List<Document> docs = this.documentRepo.getPublicDocuments(params);
        
        long totalItems = this.documentRepo.countPublicDocuments(params);
        
        int page = this.getPage(params);
        int size = this.getSize(params);
        
        List<DocumentResponseDTO> items = docs.stream()
                                            .map(DocumentResponseDTO::fromDocument)
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
    public DocumentResponseDTO getPublicDocumentById(Long documentId) {
        Document doc = this.documentRepo.getPublicDocumentById(documentId);
        
        if (doc == null){
            return null;
        }
        
        return DocumentResponseDTO.fromDocument(doc);
        
    }

    @Override
    public String validatePublicDocumentParams(Map<String, String> params) {
        if (params == null){
            return null;
        }
        
        String size = params.get("size");
        
        if (size != null && !size.trim().isEmpty()){
            try {
                int sizeValue = Integer.parseInt(size);

                if (sizeValue < 1) {
                    return "Size must be more than 0";
                }

                if (sizeValue > 20) {
                    return "Size mustn't be over 20";
                }

            } catch (NumberFormatException ex) {
                return "Size must be integer";
            }
        }
        
        if (!SortUtils.isValidSort(params, DocumentSorts.PUBLIC_DOCUMENT_SORT, DocumentSorts.DEFAULT_SORT)){
            return "Only sort by: title, publishYear, popular, newest";
        }
        
        String publishYear = params.get("publishYear");
        
        if (publishYear != null && !publishYear.trim().isEmpty()) {
            try {
                Integer.valueOf(publishYear);
            } catch (NumberFormatException ex) {
                return "publishYear must be integer";
            }
        }
        
        String premium = params.get("premium");

        if (premium != null && !premium.trim().isEmpty()) {
            if (!premium.equalsIgnoreCase("true")
                    && !premium.equalsIgnoreCase("false")) {
                return "premium only be true or false";
            }
        }

        String documentType = params.get("documentType");

        if (documentType != null && !documentType.trim().isEmpty()) {
            if (!ALLOWED_DOCUMENT_TYPES.contains(documentType.trim())) {
                return "documentType must be one of: PDF, DOCX, EPUB, VIDEO, AUDIO";
            }
        }

        String categoryId = params.get("categoryId");

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            try {
                Long id = Long.valueOf(categoryId);

                if (this.categoryRepo.getActiveCategoryById(id) == null) {
                    return "CATEGORY_NOT_FOUND";
                }

            } catch (NumberFormatException ex) {
                return "categoryId must be integer";
            }
        }

        return null;
        
        
    }

    @Override
    public List<DocumentFileResponseDTO> getPublicDocumentFiles(Long documentId) {
        Document doc = this.documentRepo.getPublicDocumentById(documentId);
        
        if (doc == null){
            return null;
        }
        
        List<DocumentFile> files = this.documentFileRepo.getFilesByDocumentId(documentId);
        
        return files.stream().map(DocumentFileResponseDTO::fromDocumentFile)
                            .collect(Collectors.toList());
    }
    
    
    
    
}
