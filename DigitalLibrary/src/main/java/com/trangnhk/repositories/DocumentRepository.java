/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.Review;
import com.trangnhk.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface DocumentRepository {
    List<Document> getPublicDocuments(Map<String, String> params);
    long countPublicDocuments(Map<String, String> params);
    Document getPublicDocumentById(Long documentId);
    
    List<Document> getManagedDocuments(User currentU, boolean isAdmin, Map<String, String> params);
    long countManagedDocument(User currentU, boolean isAdmin, Map<String, String> params);
    Document add(Document document);
    Document update(Document document);
    void delete(Document document);
    
    public boolean existsByCategoryIdAndActiveTrue(Long categoryId);
    Document getDocumentById(Long documentId);
    
}
