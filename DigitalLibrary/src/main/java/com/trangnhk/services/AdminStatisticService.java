/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.AdminStatisticResponseDTO;
import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface AdminStatisticService {
    AdminStatisticsOverViewDTO getOverview();
    AdminStatisticResponseDTO getAccessStatistics(Map<String, String> params);
    AdminStatisticResponseDTO getBorrowStatistics(Map<String, String> params, String username);
}
