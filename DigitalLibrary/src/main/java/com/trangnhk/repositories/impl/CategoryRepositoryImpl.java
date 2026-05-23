/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Category;
import com.trangnhk.repositories.CategoryRepository;
import jakarta.persistence.NoResultException;
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
public class CategoryRepositoryImpl implements CategoryRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Category> getActiveCategories(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createNamedQuery("Category.findActiveWithKeyword", Category.class);

        query.setParameter("keyword", this.getKeywordPattern(params));

        int page = this.getPage(params);
        int size = this.getSize(params);
        int start = (page - 1) * size;

        query.setFirstResult(start);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private String getKeywordPattern(Map<String, String> params) {
        if (params == null) {
            return "%%";
        }

        String keyword = params.get("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = params.get("kw");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return "%%";
        }

        return "%" + keyword.trim().toLowerCase() + "%";
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
        int defaultSize = this.env.getProperty("categories.page_size", Integer.class, 10);
        int maxSize = this.env.getProperty("categories.max_page_size", Integer.class, 20);

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
    public long countActiveCategories(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createNamedQuery("Category.countActiveWithKeyword", Long.class);

        query.setParameter("keyword", this.getKeywordPattern(params));

        return (long) query.getSingleResult();
    }

    @Override
    public Category getActiveCategoryById(Long categoryId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createNamedQuery("Category.findActiveById", Category.class);

        query.setParameter("id", categoryId);

        try {
            return (Category) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<Category> getCates() {
        Session session = this.factory.getObject().getCurrentSession();
        Query query = session.createQuery("FROM Category", Category.class);
        return query.getResultList();
    }

    @Override
    public Boolean nameExisted(String name) {
        Session session = this.factory.getObject().getCurrentSession();

        Query query = session.createQuery("SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class);
        query.setParameter("name", name);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public void addOrUpdateCategory(Category c) {
        Session s = this.factory.getObject().getCurrentSession();

        if (c.getId() == null) {
            s.persist(c);
        } else {
            s.merge(c);   
        }
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createNamedQuery("Category.findById", Category.class);

        query.setParameter("id", categoryId);

        try {
            return (Category) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;

        }
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long categoryId) {
        Session s = this.factory.getObject().getCurrentSession();

    Query query = s.createQuery(
            "SELECT COUNT(c) " +
            "FROM Category c " +
            "WHERE c.name = :name " +
            "AND c.id <> :id",
            Long.class);

    query.setParameter("name", name);
    query.setParameter("id", categoryId);
    
    Long count = (Long) query.getSingleResult();
    return count > 0;
    }
    

}
