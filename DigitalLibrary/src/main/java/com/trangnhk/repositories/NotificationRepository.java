/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Notification;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
public interface NotificationRepository{
    Notification save(Notification notification);
    
    List<Notification> getNotificationByUsername(String username, Map<String, String> params);
    long countNotificationByUsername(String username);
    long countUnReadNotificationByUsername(String username);
    Notification getNotificationById(Long notificationId);
    Notification update(Notification notification);
    
}
