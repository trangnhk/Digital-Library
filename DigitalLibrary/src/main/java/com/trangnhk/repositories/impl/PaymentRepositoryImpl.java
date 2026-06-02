/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Payment;
import com.trangnhk.pojo.enums.PaymentStatus;
import com.trangnhk.repositories.PaymentRepository;
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
public class PaymentRepositoryImpl implements PaymentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    
    @Override
    public Payment add(Payment payment) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(payment);
        
        return payment;
    }

    @Override
    public Payment update(Payment payment) {
        Session s = this.factory.getObject().getCurrentSession();
        
        return s.merge(payment);
    }
    
    @Override
    public boolean existsByDocumentId(Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createQuery("SELECT COUNT(p.id) FROM Payment p "
                + "WHERE p.document.id = :documentId ",
                Long.class);
        
        query.setParameter("documentId", documentId);
        
        Long count = (Long) query.getSingleResult();
        
        return count > 0;
        
        
    }

    @Override
    public boolean existSuccessPayment(Long userId, Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createQuery("SELECT COUNT(p.id) FROM Payment p "
                + "WHERE p.user.id = :userId "
                + "AND p.document.id = :documentId "
                + "AND p.paymentStatus = :status", Long.class);
        
        query.setParameter("userId", userId);
        query.setParameter("documentId", documentId);
        query.setParameter("status", PaymentStatus.SUCCESS);
        
        Long count = (Long) query.getSingleResult();
        
        return count > 0;
    }

    @Override
    public Payment getPaymentById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        
        return s.get(Payment.class, id);
    }

    @Override
    public Payment getPaymentByStripeSessionBy(String stripeSessionId) {
        Session s = this.factory.getObject().getCurrentSession();
        
        Query query = s.createQuery("FROM Payment p WHERE p.stripeSessionId = :stripeSessionId", Payment.class);
        
        query.setParameter("stripeSessionId", stripeSessionId);
        
        try{
            return (Payment) query.getSingleResult();
        } catch (Exception ex){
            return null;
        }
        
    }

    
    
    
    
}
