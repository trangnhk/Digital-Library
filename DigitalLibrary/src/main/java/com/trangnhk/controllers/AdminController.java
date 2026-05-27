/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.AdminStatisticResponseDTO;
import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.UserResponseDTO;
import com.trangnhk.services.AdminStatisticService;
import com.trangnhk.services.CategoryService;
import com.trangnhk.services.SecureDocumentService;
import com.trangnhk.services.UserService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Controller
public class AdminController {

    @Autowired
    private SecureDocumentService docService;

    @Autowired
    private AdminStatisticService adminStatisticService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/admin/login")
    public String loginView() {
        
        return "admin/login";
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");

        AdminStatisticsOverViewDTO overview = this.adminStatisticService.getOverview();
        model.addAttribute("overview", overview);

        return "admin/dashboard";
    }

    @GetMapping("/admin/documents")
    public String documentsView(@RequestParam Map<String, String> params, Model model) {
        PageResponseDTO documentsPage = this.docService.getAdminDocuments(params);

        model.addAttribute("title", "Documents Management");
        model.addAttribute("documentsPage", documentsPage);
        model.addAttribute("documents", documentsPage.getItems());

        model.addAttribute("keyword", params.getOrDefault("keyword", ""));
        model.addAttribute("approved", params.getOrDefault("approved", ""));
        model.addAttribute("categoryId", params.getOrDefault("categoryId", ""));
        model.addAttribute("uploadBy", params.getOrDefault("uploadBy", ""));
        model.addAttribute("sort", params.getOrDefault("sort", "newest"));
        model.addAttribute("page", params.getOrDefault("page", "1"));
        model.addAttribute("size", params.getOrDefault("size", "10"));

        model.addAttribute("categories", categoryService.getCates());

        model.addAttribute("content", "~{admin/documents :: documentsContent}");

        return "admin/documents";
    }

    @GetMapping("/admin/statistics")
    public String statistics(@RequestParam Map<String, String> params, Model model, Principal principal) {
        Map<String, String> statisticsParams = buildStatisticsParams(params);

        try {
            AdminStatisticResponseDTO accessStatistics = this.adminStatisticService.getAccessStatistics(statisticsParams);

            AdminStatisticResponseDTO borrowStatistics = this.adminStatisticService.getBorrowStatistics(statisticsParams, principal.getName());

            model.addAttribute("accessStatistics", accessStatistics);
            model.addAttribute("borrowStatistics", borrowStatistics);
            model.addAttribute("params", statisticsParams);

            return "admin/statistics";

        } catch (ResponseStatusException ex) {
            model.addAttribute("errorMessage", ex.getReason());
            model.addAttribute("errorStatus", ex.getStatusCode().value());

            Map<String, String> defaultParams = buildDefaultStatisticsParams();

            AdminStatisticResponseDTO accessStatistics = this.adminStatisticService.getAccessStatistics(defaultParams);

            AdminStatisticResponseDTO borrowStatistics = this.adminStatisticService.getBorrowStatistics(defaultParams, principal.getName());

            model.addAttribute("accessStatistics", accessStatistics);
            model.addAttribute("borrowStatistics", borrowStatistics);
            model.addAttribute("params", defaultParams);

            return "admin/statistics";
        }
    }

    private Map<String, String> buildStatisticsParams(Map<String, String> params) {
        Map<String, String> result = buildDefaultStatisticsParams();

        if (params == null) {
            return result;
        }

        String period = params.get("period");

        if (period != null && !period.trim().isEmpty()) {
            result.put("period", period.trim());
        }

        String fromDate = params.get("fromDate");

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            result.put("fromDate", fromDate.trim());
        }

        String toDate = params.get("toDate");

        if (toDate != null && !toDate.trim().isEmpty()) {
            result.put("toDate", toDate.trim());
        }

        String categoryId = params.get("categoryId");

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            result.put("categoryId", categoryId.trim());
        }

        String status = params.get("status");

        if (status != null && !status.trim().isEmpty()) {
            result.put("status", status.trim());
        }

        return result;
    }

    private Map<String, String> buildDefaultStatisticsParams() {
        String currentYear = String.valueOf(LocalDate.now().getYear());

        Map<String, String> result = new HashMap<>();

        result.put("period", "month");
        result.put("fromDate", currentYear + "-01-01");
        result.put("toDate", currentYear + "-12-31");

        return result;
    }

    @GetMapping("/admin/librarians/pending")
    public String pendingLibrarians(Model model) {

        model.addAttribute("librarians", userService.getPendingLibrarians());
        model.addAttribute("content", "~{admin/librarians :: pendingLibrariansContent}");
        return "admin/librarians";
    }

    @GetMapping("/admin/users")
    public String usersView(
            @RequestParam Map<String, String> params,
            Model model
    ) {

        List<UserResponseDTO> users = userService.getUsers(params);

        model.addAttribute("title", "Users Management");
        model.addAttribute("users", users);

        return "admin/users";
    }
}
