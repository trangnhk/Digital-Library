/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.AccessHistory;
import com.trangnhk.repositories.AccessHistoryRepository;
import jakarta.persistence.Query;
import java.util.Date;
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
public class AccessHistoryRepositoryImpl implements AccessHistoryRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public AccessHistory add(AccessHistory access) {
        Session s = this.factory.getObject().getCurrentSession();
        
        s.persist(access);
        
        return access;
    }

    // Prevent SPAM
    @Override
    public boolean existRecentAccess(Long userId, Long documentId, String ipAddress, int seconds) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Date threshold = new Date(System.currentTimeMillis() - seconds * 1000L);
        
        Query query = s.createQuery("SELECT COUNT(a.id) FROM AccessHistory a" +
                " WHERE a.user.id = :userId" +
                " AND a.document.id = :documentId" +
                " AND a.ipAddress = :ipAddress" +
                " AND a.accessTime >= :threshold", Long.class);
        
        query.setParameter("userId", userId);
        query.setParameter("documentId", documentId);
        query.setParameter("ipAddress", ipAddress);
        query.setParameter("threshold", threshold);
        
        Long count = (Long) query.getSingleResult();
        
        return count > 0;
    }
    
    
    
    
    
}
