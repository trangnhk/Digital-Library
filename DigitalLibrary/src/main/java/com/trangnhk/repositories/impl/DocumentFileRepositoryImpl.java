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
public class DocumentFileRepositoryImpl implements DocumentFileRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<DocumentFile> getFilesByDocumentId(Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createNamedQuery("DocumentFile.findByDocumentId", DocumentFile.class);

        query.setParameter("documentId", documentId);

        return query.getResultList();

    }

    @Override
    public DocumentFile add(DocumentFile file) {
        Session s = this.factory.getObject().getCurrentSession();

        s.persist(file);

        return file;
    }

    @Override
    public DocumentFile update(DocumentFile file) {
        Session s = this.factory.getObject().getCurrentSession();
        
        return s.merge(file);
    }

    @Override
    public DocumentFile getFileByIdAndDocumnetId(Long fileId, Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("FROM DocumentFile f "
                + "WHERE f.id = :fileId "
                + "AND f.document.id = :documentId "
                + "AND (f.active = true OR f.active IS NULL)", DocumentFile.class);

        query.setParameter("fileId", fileId);
        query.setParameter("documentId", documentId);

        try {
            return (DocumentFile) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<DocumentFile> getAllFilesByDocumentId(Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createNamedQuery("DocumentFile.findAllByDocumentId", DocumentFile.class);
        
        query.setParameter("documentId", documentId);
        
        return query.getResultList();
    }

}
