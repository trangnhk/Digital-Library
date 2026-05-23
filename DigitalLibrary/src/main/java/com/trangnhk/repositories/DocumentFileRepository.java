/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.DocumentFile;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface DocumentFileRepository {
    List<DocumentFile> getFilesByDocumentId(Long documentId);
    DocumentFile add(DocumentFile file);
    void deleteByDocumentId(Long documentId);
}
