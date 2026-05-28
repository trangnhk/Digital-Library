/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.pojo.Notification;
import com.trangnhk.repositories.NotificationRepository;
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
@Transactional
@PropertySource("classpath:configs.properties")
public class NotificationRepositoryImpl implements NotificationRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Override
    public Notification save(Notification notification) {
        Session s = this.factory.getObject().getCurrentSession();

        s.persist(notification);
        return notification;
    }

    @Override
    public List<Notification> getNotificationByUsername(String username, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("FROM Notification n WHERE n.user.username = :username ORDER BY n.createdDate DESC", Notification.class);

        query.setParameter("username", username);

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
        int defaultSize = this.env.getProperty("notifications.page_size", Integer.class, 10);
        int maxSize = this.env.getProperty("notifications.max_page_size", Integer.class, 20);

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
    public long countNotificationByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("SELECT COUNT(n.id) FROM Notification n WHERE n.user.username = :username", Long.class);

        query.setParameter("username", username);

        return (long) query.getSingleResult();

    }

    @Override
    public long countUnReadNotificationByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("SELECT COUNT(n.id) FROM Notification n WHERE n.user.username = :username AND n.isRead = false", Long.class);

        query.setParameter("username", username);

        return (long) query.getSingleResult();
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("FROM Notification n WHERE n.id = :id", Notification.class);

        query.setParameter("id", notificationId);

        try {

            return (Notification) query.getSingleResult();

        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public Notification update(Notification notification) {
        Session s = this.factory.getObject().getCurrentSession();
        
        return s.merge(notification);
        
        
    }

}
