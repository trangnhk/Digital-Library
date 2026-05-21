/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.DocumentFile;
import com.trangnhk.repositories.DocumentFileRepository;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public class DocumentFileRepositoryImpl implements DocumentFileRepository{

    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<DocumentFile> getFilesByDocumentId(Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createNamedQuery("DocumentFile.findByDocumentId", DocumentFile.class);
        
        query.setParameter("documentId", documentId);
        
        return query.getResultList();
        
    }
    
}
