/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Notification;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
public interface NotificationRepository{
    Notification save(Notification notification);
}
