/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Repository
@PropertySource("classpath:configs.properties")
//@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

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

        int defaultSize = this.env.getProperty("users.page_size", Integer.class, 10);

        int maxSize = this.env.getProperty("users.max_page_size", Integer.class, 20);

        if (params == null) {
            return defaultSize;
        }

        try {

            int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(defaultSize)));

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
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        Query query = s.createNamedQuery("User.findByUsername", User.class);

        query.setParameter("username", username);

        try {
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User addUser(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);

        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public boolean existEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        Query query = s.createNamedQuery("User.findByEmail", User.class);

        query.setParameter("email", email);

        try {
            return (Integer) query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean existPhone(String phone) {
        Session s = this.factory.getObject().getCurrentSession();
        Query query = s.createNamedQuery("User.findByPhone", User.class);

        query.setParameter("phone", phone);

        try {
            return (Integer) query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User update(User u) {
        Session s = this.factory.getObject().getCurrentSession();

        User updateU = s.merge(u);

        return updateU;
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {

        Session session = this.factory
                .getObject()
                .getCurrentSession();

        CriteriaBuilder b = session.getCriteriaBuilder();

        CriteriaQuery<User> q = b.createQuery(User.class);

        Root<User> root = q.from(User.class);

        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            // keyword
            String keyword = params.get("keyword");

            if (keyword != null && !keyword.isEmpty()) {

                Predicate usernameLike = (Predicate) b.like(
                        b.lower(root.get("username")),
                        String.format("%%%s%%", keyword.toLowerCase())
                );

                Predicate emailLike = (Predicate) b.like(
                        b.lower(root.get("email")),
                        String.format("%%%s%%", keyword.toLowerCase())
                );

                predicates.add(b.or(usernameLike, emailLike));
            }

            // role
            String role = params.get("role");

            if (role != null && !role.isEmpty()) {

                try {

                    UserRole userRole = UserRole.valueOf(role);

                    predicates.add((Predicate) b.equal(root.get("role"), userRole));

                } catch (IllegalArgumentException ex) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid role"
                    );
                }
            }

            // active
            String active = params.get("active");

            if (active != null && !active.isEmpty()) {

                predicates.add((Predicate) b.equal(
                        root.get("active"),
                        Boolean.parseBoolean(active)
                ));
            }

            q.where((jakarta.persistence.criteria.Predicate[]) predicates.toArray(Predicate[]::new));
        }

        // sort
        String sort = params.get("sort");

        if ("username_asc".equals(sort)) {
            q.orderBy(b.asc(root.get("username")));
        } else if ("username_desc".equals(sort)) {
            q.orderBy(b.desc(root.get("username")));

        } else {
            q.orderBy(b.asc(root.get("id")));
        }

        Query query = session.createQuery(q);
        if (params != null) {
            int pageSize = this.env.getProperty("users.page_size",Integer.class,10);
            int maxPageSize = this.env.getProperty( "users.max_page_size",Integer.class,20);
            int page;

            try {
                page = Integer.parseInt(
                        params.getOrDefault("page", "1")
                );
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException ex) {

                page = 1;
            }
            int size;
            try {
                size = Integer.parseInt( params.getOrDefault("size",String.valueOf(pageSize)));
                if (size < 1) {
                    size = pageSize;
                }
                if (size > maxPageSize) {
                    size = maxPageSize;
                }
            } catch (NumberFormatException ex) {

                size = pageSize;
            }

            int start = (page - 1) * size;

            query.setFirstResult(start);

            query.setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public User getUserById(Long userId) {
        Session session = this.factory.getObject().getCurrentSession();

        Query query = session.createQuery(
                "FROM User u WHERE u.id = :id",
                User.class
        );

        query.setParameter("id", userId);

        try {

            return (User) query.getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<User> getPendingLibrarians() {
        Session session = this.factory.getObject().getCurrentSession();

        Query query = session.createQuery(
                "FROM User u "
                + "WHERE u.role = :role "
                + "AND u.librarianVerified = false",
                User.class
        );

        query.setParameter("role", UserRole.ROLE_LIBRARIAN);
        return query.getResultList();
    }

}
