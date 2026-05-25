/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.User;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.utils.AdminDocumentSorts;
import com.trangnhk.utils.DocumentSorts;
import com.trangnhk.utils.SortUtils;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class DocumentRepositoryImpl implements DocumentRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public List<Document> getPublicDocuments(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        StringBuilder hql = new StringBuilder();

        hql.append("SELECT d from Document d ");
        hql.append("WHERE d.approved = true ");

        this.appendFilters(hql, params);

        hql.append(SortUtils.buildOrderBy(params, DocumentSorts.PUBLIC_DOCUMENT_SORT, DocumentSorts.DEFAULT_SORT));

        Query query = s.createQuery(hql.toString(), Document.class);

        this.setFilterParameters(query, params);

        int page = this.getPage(params);
        int size = this.getSize(params);
        int start = (page - 1) * size;

        query.setFirstResult(start);
        query.setMaxResults(size);

        return query.getResultList();
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
        int defaultSize = this.env.getProperty("documents.page_size", Integer.class, 10);
        int maxSize = this.env.getProperty("documents.max_page_size", Integer.class, 20);

        if (params == null) {
            return defaultSize;
        }

        try {
            int size = Integer.parseInt(
                    params.getOrDefault("size", String.valueOf(defaultSize))
            );

            if (size < 1) {
                return defaultSize;
            }

            if (size > maxSize) {
                return maxSize;
            }

            return size;

        } catch (NumberFormatException ex) {
            return defaultSize;
        }
    }

    @Override
    public long countPublicDocuments(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        StringBuilder hql = new StringBuilder();

        hql.append("SELECT COUNT(d.id) FROM Document d ");
        hql.append("WHERE d.approved = true ");

        this.appendFilters(hql, params);

        Query query = s.createQuery(hql.toString(), Long.class);

        this.setFilterParameters(query, params);

        return (long) query.getSingleResult();

    }

    private void appendFilters(StringBuilder hql, Map<String, String> params) {
        if (params == null) {
            return;
        }

        String keyword = params.get("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(d.title) LIKE :keyword ");
            hql.append("OR LOWER(d.description) LIKE :keyword) ");
        }

        String author = params.get("author");
        if (author != null && !author.trim().isEmpty()) {
            hql.append("AND LOWER(d.author) LIKE :author ");
        }

        String categoryId = params.get("categoryId");
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            hql.append("AND d.category.id = :categoryId ");
        }

        String publishYear = params.get("publishYear");
        if (publishYear != null && !publishYear.trim().isEmpty()) {
            hql.append("AND d.publishYear = :publishYear ");
        }

        String documentType = params.get("documentType");
        if (documentType != null && !documentType.trim().isEmpty()) {
            hql.append("AND d.documentType = :documentType ");
        }

        String premium = params.get("premium");
        if (premium != null && !premium.trim().isEmpty()) {
            hql.append("AND d.premium = :premium ");
        }
    }

    private void setFilterParameters(Query query, Map<String, String> params) {
        if (params == null) {
            return;
        }

        String keyword = params.get("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }

        String author = params.get("author");
        if (author != null && !author.trim().isEmpty()) {
            query.setParameter("author", "%" + author.trim().toLowerCase() + "%");
        }

        String categoryId = params.get("categoryId");
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            query.setParameter("categoryId", Long.valueOf(categoryId));
        }

        String publishYear = params.get("publishYear");
        if (publishYear != null && !publishYear.trim().isEmpty()) {
            query.setParameter("publishYear", Integer.valueOf(publishYear));
        }

        String documentType = params.get("documentType");
        if (documentType != null && !documentType.trim().isEmpty()) {
            query.setParameter("documentType", documentType.trim());
        }

        String premium = params.get("premium");
        if (premium != null && !premium.trim().isEmpty()) {
            query.setParameter("premium", Boolean.valueOf(premium));
        }
    }

    @Override
    public Document getPublicDocumentById(Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("SELECT d FROM Document d WHERE d.id = :id AND d.approved = true", Document.class);

        query.setParameter("id", documentId);

        try {
            return (Document) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public List<Document> getManagedDocuments(User currentU, boolean isAdmin, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder();

        hql.append("SELECT d FROM Document d WHERE 1 = 1 ");

        if (!isAdmin) {
            hql.append(" AND d.uploadedBy.id = :userId");
        }

        this.appendManagedFilters(hql, params);

        hql.append(" ORDER BY d.createdDate DESC");

        Query query = s.createQuery(hql.toString(), Document.class);

        if (!isAdmin) {
            query.setParameter("userId", currentU.getId());
        }

        this.setManagedFilterParameters(query, params);

        int page = this.getPage(params);
        int size = this.getSize(params);
        int start = (page - 1) * size;

        query.setFirstResult(start);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private void appendManagedFilters(StringBuilder hql, Map<String, String> params) {
        if (params == null) {
            return;
        }

        String keyword = params.get("keyword");

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" AND (LOWER(d.title) LIKE :keyword ");
            hql.append(" OR LOWER(d.description) LIKE :keyword) ");
        }

        String approved = params.get("approved");

        if (approved != null && !approved.trim().isEmpty()) {
            hql.append(" AND d.approved = :approved ");
        }
    }

    private void setManagedFilterParameters(Query query, Map<String, String> params) {
        if (params == null) {
            return;
        }

        String keyword = params.get("keyword");

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }

        String approved = params.get("approved");

        if (approved != null && !approved.trim().isEmpty()) {
            query.setParameter("approved", Boolean.valueOf(approved));
        }

    }

    @Override
    public long countManagedDocument(User currentU, boolean isAdmin, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder();

        hql.append("SELECT COUNT(d.id) FROM Document d WHERE 1 = 1 ");

        if (!isAdmin) {
            hql.append(" AND d.uploadedBy.id = :userId");
        }

        this.appendManagedFilters(hql, params);

        Query query = s.createQuery(hql.toString(), Long.class);

        if (!isAdmin) {
            query.setParameter("userId", currentU.getId());
        }

        this.setManagedFilterParameters(query, params);

        return (long) query.getSingleResult();

    }

    @Override
    public Document add(Document document) {
        Session s = this.factory.getObject().getCurrentSession();

        s.persist(document);

        return document;
    }

    @Override
    public boolean existsByCategoryIdAndActiveTrue(Long categoryId) {

        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery(
                "SELECT COUNT(d.id) "
                + "FROM Document d "
                + "WHERE d.category.id = :categoryId "
                + "AND d.approved = true",
                Long.class
        );

        query.setParameter("categoryId", categoryId);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    @Override
    public Document getDocumentById(Long documentId) {
        Session session = this.factory.getObject().getCurrentSession();

        Query query = session.createQuery(
                "FROM Document d "
                + "WHERE d.id = :id", Document.class);
        
        query.setParameter("id", documentId);
        
        try{
            return (Document) query.getSingleResult();
        } catch (Exception ex){
            return null;
        }
    }

    @Override
    public Document update(Document document) {
        Session s = this.factory.getObject().getCurrentSession();
        
        if (document.getId() == null){
            s.persist(document);
            return document;
        }
        
        return s.merge(document);
    }

    @Override
    public void delete(Document document) {
        Session s = this.factory.getObject().getCurrentSession();
        
        s.remove(document);
    }

    @Override
    public List<Document> getAdminDocuments(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        
        StringBuilder hql = new StringBuilder();

        hql.append("SELECT d FROM Document d WHERE 1 = 1 ");

        this.appendAdminDocumentFilters(hql, params);
        
        hql.append(SortUtils.buildOrderBy(params, AdminDocumentSorts.ADMIN_DOCUMENT_SORT, AdminDocumentSorts.DEFAULT_SORT));
        
        Query query = s.createQuery(hql.toString(), Document.class);
        
        this.setAdminDocumentFilterParams(query, params);
        
        int page = this.getPage(params);
        int size = this.getSize(params);
        int start = (page - 1) * size;
        
        query.setFirstResult(start);
        query.setMaxResults(size);
        
        return query.getResultList();
        
        
    }
    
    private void appendAdminDocumentFilters(StringBuilder hql, Map<String, String> params){
        if (params == null){
            return;
        }
        
        String kw = params.get("keyword");
        if (kw != null && !kw.trim().isEmpty()){
            hql.append(" AND (LOWER(d.title) LIKE :keyword ");
            hql.append(" OR LOWER(d.author) LIKE :keyword ");
            hql.append(" AND (LOWER(d.publisher) LIKE :keyword) ");
            
        }
        
        String approved = params.get("approved");
        if (approved != null && !approved.trim().isEmpty()){
            hql.append(" AND d.approved = :approved ");
        }
        
        String cateId = params.get("categoryId");
        if (cateId != null && !cateId.trim().isEmpty()){
            hql.append(" AND d.category.id = :categoryId ");
        }
        
        String uploadBy = params.get("uploadBy");
        if (uploadBy != null && !uploadBy.trim().isEmpty()){
            hql.append(" AND d.uploadedBy.id = :uploadBy ");
        }
        
        
    }

    private void setAdminDocumentFilterParams(Query query, Map<String, String> params){
        if (params == null){
            return;
        }
        
        String kw = params.get("keyword");
        if (kw != null && !kw.trim().isEmpty()){
            query.setParameter("keyword", "%" + kw.trim().toLowerCase() + "%");
        }
        
        String approved = params.get("approved");
        if (approved != null && !approved.trim().isEmpty()){
            query.setParameter("approved", Boolean.valueOf(approved));
        }
        
        String cateId = params.get("categoryId");
        if (cateId != null && !cateId.trim().isEmpty()){
            query.setParameter("categoryId", Long.valueOf(cateId));
        }
        
        String uploadBy = params.get("uploadBy");
        if (uploadBy != null && !uploadBy.trim().isEmpty()){
            query.setParameter("uplaodBy", Long.valueOf(uploadBy));
        }
    }
    
    @Override
    public long countAdminDocuments(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        
        StringBuilder hql = new StringBuilder();
        
        hql.append("SELECT COUNT(d.id) FROM Document d WHERE 1 = 1");
        
        this.appendAdminDocumentFilters(hql, params);
        
        Query query = s.createQuery(hql.toString(), Long.class);
        
        this.setAdminDocumentFilterParams(query, params);
        
        return (long) query.getSingleResult();
    }


}
