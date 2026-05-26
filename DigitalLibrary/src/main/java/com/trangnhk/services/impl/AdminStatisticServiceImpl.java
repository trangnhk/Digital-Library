/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AdminStatisticPointDTO;
import com.trangnhk.dto.AdminStatisticResponseDTO;
import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.repositories.AdminStatisticRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.AdminStatisticService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    
    
    private List<AdminStatisticPointDTO> convertRowsToPoints(String period, List<Object[]> rows) {
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
    @Transactional(readOnly = true)
    public AdminStatisticResponseDTO getBorrowStatistics(Map<String, String> params, String username) {
        
        String period = getRequiredPeriod(params);
        LocalDate fromLocalDate = getRequiredDate(params, "fromDate");
        LocalDate toLocalDate = getRequiredDate(params, "toDate");

        validateDateRange(fromLocalDate, toLocalDate);

        BorrowStatus status = getOptionalBorrowStatus(params);

        User currentUser = this.userRepo.getUserByUsername(username);

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated.");
        }

        String role = currentUser.getRole().name();

        boolean isAdmin = "ROLE_ADMIN".equals(role);
        boolean isLibrarian = "ROLE_LIBRARIAN".equals(role);

        if (!isAdmin && !isLibrarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or librarian can view borrow statistics.");
        }

        String ownerUsername = null;

        if (isLibrarian) {
            ownerUsername = currentUser.getUsername();
        }

        Date fromDate = toStartDate(fromLocalDate);
        Date toDate = toEndDate(toLocalDate);

        List<Object[]> rows;

        if ("month".equals(period)) {
            rows = this.adminStatisticRepo.countBorrowsByMonth(fromDate, toDate, status, ownerUsername);
        } 
        else {
            rows = this.adminStatisticRepo.countBorrowsByYear(fromDate, toDate, status, ownerUsername);
        }

        List<AdminStatisticPointDTO> data = convertRowsToPoints(period, rows);

        Long total = calculateTotal(data);
        
        return new AdminStatisticResponseDTO("borrows", period, fromLocalDate.toString(), toLocalDate.toString(), status != null ? status.name() : null, null, total, data);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatisticResponseDTO getAccessStatistics(Map<String, String> params) {
        String period = this.getRequiredPeriod(params);
        
        LocalDate fromLocalDate = this.getRequiredDate(params, "fromDate");
        LocalDate toLocalDate = this.getRequiredDate(params, "toDate");

        this.validateDateRange(fromLocalDate, toLocalDate);

        Long categoryId = this.getOptionalLong(params, "categoryId");

        Date fromDate = this.toStartDate(fromLocalDate);
        Date toDate = this.toEndDate(toLocalDate);

        List<Object[]> rows;

        if ("month".equals(period)) {
            rows = this.adminStatisticRepo.countAccessByMonth(fromDate, toDate, categoryId);
        } else {
            rows = this.adminStatisticRepo.countAccessByYear(fromDate, toDate, categoryId);
        }

        List<AdminStatisticPointDTO> data = convertRowsToPoints(period, rows);

        Long total = calculateTotal(data);

        
        return new AdminStatisticResponseDTO("access", period, fromLocalDate.toString(), toLocalDate.toString(), null, total, categoryId, data);
        
    }
    
    private String getRequiredPeriod(Map<String, String> params){
        String period = params.get("period");
        
        if (period == null || period.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "period is required. Allowed values: month, year.");
        }
        
        period = period.trim().toLowerCase();

        if (!period.equals("month") && !period.equals("year")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period. Allowed values: month, year.");
        }

        return period;
    }
    
    private LocalDate getRequiredDate(Map<String, String> params, String key) {
        String value = params.get(key);

        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " is required. Format: yyyy-MM-dd.");
        }

        try {
            return LocalDate.parse(value.trim());
            
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must have format yyyy-MM-dd.");
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate must be less than or equal to toDate.");
        }
    }

    private Long getOptionalLong(Map<String, String> params, String key) {
        String value = params.get(key);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(value.trim());
            
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must be a number.");
        }
    }

    private BorrowStatus getOptionalBorrowStatus(Map<String, String> params) {
        String value = params.get("status");

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return BorrowStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status. Allowed values: BORROWING, RETURNED, EXPIRED.");
        }
    }

    private Date toStartDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date toEndDate(LocalDate localDate) {
        return Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }


}
