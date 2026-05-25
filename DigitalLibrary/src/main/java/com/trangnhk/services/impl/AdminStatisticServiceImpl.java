/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import com.trangnhk.repositories.AdminStatisticRepository;
import com.trangnhk.services.AdminStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */

@Service
@Transactional
public class AdminStatisticServiceImpl implements AdminStatisticService{
    
    @Autowired
    private AdminStatisticRepository adminStatisticRepo;

    @Override
    @Transactional(readOnly = true)
    public AdminStatisticsOverViewDTO getOverview() {
        Long totalUsers = this.adminStatisticRepo.countTotalUsers();

        Long totalDocuments = this.adminStatisticRepo.countTotalDocuments();

        Long totalApprovedDocuments = this.adminStatisticRepo.countTotalApprovedDocuments();

        Long totalPendingLibrarians = this.adminStatisticRepo.countTotalPendingLibrarians();

        Long totalBorrows = this.adminStatisticRepo.countTotalBorrows();

        Long totalAccesses = this.adminStatisticRepo.countTotalAccesses();

        return new AdminStatisticsOverViewDTO(totalUsers, totalDocuments, totalApprovedDocuments, totalPendingLibrarians, totalBorrows, totalAccesses);
    }
    
}
