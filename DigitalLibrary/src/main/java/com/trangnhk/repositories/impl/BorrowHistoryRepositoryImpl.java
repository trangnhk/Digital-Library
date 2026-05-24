/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.repositories.BorrowHistoryRepository;
import jakarta.persistence.Query;
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
public class BorrowHistoryRepositoryImpl implements BorrowHistoryRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public BorrowHistory add(BorrowHistory borrow) {
        Session s = this.factory.getObject().getCurrentSession();
        
        s.persist(borrow);
        
        return borrow;
    }

    @Override
    public boolean existOpenBorrow(Long userId, Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createQuery("SELECT COUNT(b.id) FROM BorrowHistory b "
                + "WHERE b.user.id = :userId "
                + "AND b.document.id = :documentId "
                + "AND b.status = :status", Long.class);
        
        
        query.setParameter("userId", userId);
        query.setParameter("documentId", documentId);
        query.setParameter("status", BorrowStatus.BORROWING);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }
    
    
    
    
}
