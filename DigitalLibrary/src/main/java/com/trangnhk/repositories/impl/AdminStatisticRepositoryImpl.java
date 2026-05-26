/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.AdminStatisticRepository;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
public class AdminStatisticRepositoryImpl implements AdminStatisticRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Long countTotalUsers() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("SELECT COUNT(u.id) FROM User u", Long.class).getSingleResult();
    }

    @Override
    public Long countTotalDocuments() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("SELECT COUNT(d.id) FROM Document d", Long.class).getSingleResult();
    }

    @Override
    public Long countTotalApprovedDocuments() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("SELECT COUNT(d.id) FROM Document d WHERE d.approved = true", Long.class).getSingleResult();
    }

    @Override
    public Long countTotalPendingLibrarians() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("""
                SELECT COUNT(u.id)
                FROM User u
                WHERE u.role = :role
                AND (u.librarianVerified = false OR u.librarianVerified IS NULL)
                """, Long.class)
                .setParameter("role", UserRole.ROLE_LIBRARIAN)
                .getSingleResult();
    }

    @Override
    public Long countTotalBorrows() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("SELECT COUNT(b.id) FROM BorrowHistory b", Long.class).getSingleResult();
    }

    @Override
    public Long countTotalAccesses() {
        Session session = this.factory.getObject().getCurrentSession();

        return session.createQuery("SELECT COUNT(a.id) FROM AccessHistory a", Long.class).getSingleResult();
    }

    @Override
    public List<Object[]> countAccessByMonth(Date fromDate, Date toDate, Long categoryId) {
        Session session = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder("""
            SELECT
                FUNCTION('year', a.accessTime),
                FUNCTION('month', a.accessTime),
                COUNT(a.id)
            FROM AccessHistory a
            JOIN a.document d
            WHERE a.accessTime BETWEEN :fromDate AND :toDate
        """);

        if (categoryId != null) {
            hql.append(" AND d.category.id = :categoryId ");
        }

        hql.append("""
            GROUP BY FUNCTION('year', a.accessTime), FUNCTION('month', a.accessTime)
            ORDER BY FUNCTION('year', a.accessTime), FUNCTION('month', a.accessTime)
        """);

        Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> countAccessByYear(Date fromDate, Date toDate, Long categoryId) {
        Session session = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder("""
            SELECT
                FUNCTION('year', a.accessTime),
                COUNT(a.id)
            FROM AccessHistory a
            JOIN a.document d
            WHERE a.accessTime BETWEEN :fromDate AND :toDate
        """);

        if (categoryId != null) {
            hql.append(" AND d.category.id = :categoryId ");
        }

        hql.append("""
            GROUP BY FUNCTION('year', a.accessTime)
            ORDER BY FUNCTION('year', a.accessTime)
        """);

        Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> countBorrowsByMonth(Date fromDate, Date toDate, BorrowStatus status, String ownerUsername) {
        Session session = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder("""
            SELECT
                FUNCTION('year', b.borrowDate),
                FUNCTION('month', b.borrowDate),
                COUNT(b.id)
            FROM BorrowHistory b
            JOIN b.document d
            WHERE b.borrowDate BETWEEN :fromDate AND :toDate
        """);

        if (status != null) {
            hql.append(" AND b.status = :status ");
        }

        if (ownerUsername != null && !ownerUsername.trim().isEmpty()) {
            hql.append(" AND d.uploadedBy.username = :ownerUsername ");
        }

        hql.append("""
            GROUP BY FUNCTION('year', b.borrowDate), FUNCTION('month', b.borrowDate)
            ORDER BY FUNCTION('year', b.borrowDate), FUNCTION('month', b.borrowDate)
        """);

        Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (status != null) {
            query.setParameter("status", status);
        }

        if (ownerUsername != null && !ownerUsername.trim().isEmpty()) {
            query.setParameter("ownerUsername", ownerUsername);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> countBorrowsByYear(Date fromDate, Date toDate, BorrowStatus status, String ownerUsername) {
        Session session = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder("""
            SELECT
                FUNCTION('year', b.borrowDate),
                COUNT(b.id)
            FROM BorrowHistory b
            JOIN b.document d
            WHERE b.borrowDate BETWEEN :fromDate AND :toDate
        """);

        if (status != null) {
            hql.append(" AND b.status = :status ");
        }

        if (ownerUsername != null && !ownerUsername.trim().isEmpty()) {
            hql.append(" AND d.uploadedBy.username = :ownerUsername ");
        }

        hql.append("""
            GROUP BY FUNCTION('year', b.borrowDate)
            ORDER BY FUNCTION('year', b.borrowDate)
        """);

        Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        if (status != null) {
            query.setParameter("status", status);
        }

        if (ownerUsername != null && !ownerUsername.trim().isEmpty()) {
            query.setParameter("ownerUsername", ownerUsername);
        }

        return query.getResultList();
    }

}
