/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.AccessHistory;

/**
 *
 * @author Admin
 */
public interface AccessHistoryRepository {
    AccessHistory add(AccessHistory access);
    boolean existRecentAccess(Long userId, Long documentId, String ipAddress, int seconds);
}
