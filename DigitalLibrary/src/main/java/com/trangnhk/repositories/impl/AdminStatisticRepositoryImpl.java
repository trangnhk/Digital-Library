/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.AdminStatisticRepository;
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
public class AdminStatisticRepositoryImpl implements AdminStatisticRepository{

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
    
}
