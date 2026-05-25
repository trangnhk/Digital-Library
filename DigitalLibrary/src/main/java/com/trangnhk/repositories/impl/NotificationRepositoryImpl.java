/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Notification;
import com.trangnhk.repositories.NotificationRepository;
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
public class NotificationRepositoryImpl implements NotificationRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Notification save(Notification notification) {
        Session s = this.factory.getObject().getCurrentSession();
        
        s.persist(notification);
        return notification;
    }
    
}
