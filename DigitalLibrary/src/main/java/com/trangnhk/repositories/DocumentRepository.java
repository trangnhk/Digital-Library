/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Document;
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
}
