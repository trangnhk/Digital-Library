/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AdminStatisticPointDTO;
import com.trangnhk.dto.AdminStatisticResponseDTO;
import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import com.trangnhk.repositories.AdminStatisticRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.AdminStatisticService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class AdminStatisticServiceImpl implements AdminStatisticService {

    @Autowired
    private AdminStatisticRepository adminStatisticRepo;

    @Autowired
    private UserRepository userRepo;

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

    
    
    private List<AdminStatisticPointDTO> convertRowsToPoints(String period,
                                                        List<Object[]> rows) {
        List<AdminStatisticPointDTO> data = new ArrayList<>();

        for (Object[] row : rows) {
            if ("month".equals(period)) {
                Integer year = ((Number) row[0]).intValue();
                Integer month = ((Number) row[1]).intValue();
                Long total = ((Number) row[2]).longValue();

                String label = String.format("%04d-%02d", year, month);

                data.add(new AdminStatisticPointDTO(label, total));
            } else {
                Integer year = ((Number) row[0]).intValue();
                Long total = ((Number) row[1]).longValue();

                data.add(new AdminStatisticPointDTO(String.valueOf(year), total));
            }
        }

        return data;
    }
    
    private Long calculateTotal(List<AdminStatisticPointDTO> data) {
        Long total = 0L;

        for (AdminStatisticPointDTO point : data) {
            total += point.getTotal();
        }

        return total;
    }

    @Override
    public AdminStatisticResponseDTO getBorrowStatistics(Map<String, String> params, String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AdminStatisticResponseDTO getAccessStatistics(Map<String, String> params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
