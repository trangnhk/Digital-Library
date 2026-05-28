/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.NotificationPageResponseDTO;
import com.trangnhk.dto.NotificationResponseDTO;
import com.trangnhk.pojo.Notification;
import com.trangnhk.pojo.User;
import com.trangnhk.repositories.NotificationRepository;
import com.trangnhk.services.NotificationService;
import com.trangnhk.services.UserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notiRepo;

    @Autowired
    private UserService userService;

    @Override
    public NotificationPageResponseDTO getMyNotifications(String username, Map<String, String> params) {
        User currentU = this.userService.getUserByUsername(username);

        List<Notification> notis = this.notiRepo.getNotificationByUsername(username, params);

        Long totalItems = this.notiRepo.countNotificationByUsername(username);
        Long unReadCount = this.notiRepo.countUnReadNotificationByUsername(username);

        int page = this.getPage(params);
        int size = this.getSize(params);
        
        List<NotificationResponseDTO> items = notis.stream().map(NotificationResponseDTO::fromNotification)
                                                            .collect(Collectors.toList());
        
        return new NotificationPageResponseDTO(items, page, size, totalItems, page, unReadCount);
        
        
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
        if (params == null) {
            return 10;
        }

        try {
            int size = Integer.parseInt(params.getOrDefault("size", "10"));

            if (size < 1) {
                return 10;
            }

            if (size > 20) {
                return 20;
            }

            return size;

        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    @Override
    public NotificationResponseDTO readNotification(String username, Long notificationId) {
        User currentU = this.userService.getUserByUsername(username);
        
        Notification noti = this.notiRepo.getNotificationById(notificationId);
        
        if (noti == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        
        if (noti.getUser() == null || !noti.getUser().getId().equals(currentU.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You font have permission to read this notification");
        }
        
        noti.setIsRead(Boolean.TRUE);
        
        Notification updatedNoti = this.notiRepo.update(noti);
        
        return NotificationResponseDTO.fromNotification(noti);
    }

    @Override
    public String validateNotificationParams(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        String size = params.get("size");

        if (size != null && !size.trim().isEmpty()) {
            try {
                int sizeValue = Integer.parseInt(size);

                if (sizeValue < 1) {
                    return "Size must be more than 0";
                }

                if (sizeValue > 20) {
                    return "Size mustn't be over 20";
                }

            } catch (NumberFormatException ex) {
                return "Size must be integer";
            }
        }
        
        return null;
    }

}
