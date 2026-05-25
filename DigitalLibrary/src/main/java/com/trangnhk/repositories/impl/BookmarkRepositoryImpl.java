/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Bookmark;
import com.trangnhk.repositories.BookmarkRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author user
 */
@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class BookmarkRepositoryImpl implements BookmarkRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

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
        int defaultSize = this.env.getProperty("bookmarks.page_size", Integer.class, 10);
        int maxSize = this.env.getProperty("bookmark.max_page_size", Integer.class, 20);

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
    public List<Bookmark> getMyBookmarks(String username, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(Bookmark.class);
        Root<Bookmark> root = query.from(Bookmark.class);

        query.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("user").get("username"), username));
        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(builder.desc(root.get("createdDate")));

        Query<Bookmark> q = session.createQuery(query);

        int page = this.getPage(params);
        int size = this.getSize(params);
        int start = (page - 1) * size;

        q.setFirstResult(start);
        q.setMaxResults(size);

        return q.getResultList();
    }

    @Override
    public void addBookmark(Bookmark bookmark) {
        Session session = this.factory.getObject().getCurrentSession();
        if (bookmark.getId() == null) {
            session.persist(bookmark);
        }
    }

    // kiem tra ton tai bookmark vs user nay chua
    @Override
    public boolean existsByUserAndDocument(Long userId, Long documentId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root root = query.from(Bookmark.class);

        query.select(builder.count(root));

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get("user").get("id"), userId));
        predicates.add(builder.equal(root.get("document").get("id"), documentId));

        query.where(predicates.toArray(Predicate[]::new));

        Long count = session.createQuery(query).getSingleResult();

        return count > 0;
    }

    @Override
    public Bookmark getByUserAndDocument(Long userId, Long documentId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Bookmark> query = builder.createQuery(Bookmark.class);
        Root<Bookmark> root = query.from(Bookmark.class);

        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get("user").get("id"), userId));
        predicates.add(builder.equal(root.get("document").get("id"), documentId));

        query.where(predicates.toArray(Predicate[]::new));

        List<Bookmark> results = session.createQuery(query).getResultList();

        return results.isEmpty()? null: results.get(0);
    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {
        Session session = this.factory.getObject().getCurrentSession();      
        session.remove(bookmark);
    }

}
