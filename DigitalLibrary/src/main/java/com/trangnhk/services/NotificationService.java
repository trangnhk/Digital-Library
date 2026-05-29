/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.NotificationPageResponseDTO;
import com.trangnhk.dto.NotificationResponseDTO;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface NotificationService {
    NotificationPageResponseDTO getMyNotifications(String username, Map<String, String> params);
    NotificationResponseDTO readNotification(String username, Long notificationId);
    String validateNotificationParams(Map<String, String> params);
}
