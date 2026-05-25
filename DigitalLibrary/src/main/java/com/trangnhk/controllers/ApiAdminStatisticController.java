/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.AdminStatisticsOverViewDTO;
import com.trangnhk.services.AdminStatisticService;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */

@RestController
@RequestMapping("/api/secure/admin/statistics")
public class ApiAdminStatisticController {
    
    @Autowired
    private AdminStatisticService adminStatisticService;
    
    @GetMapping("/overview")
    public ResponseEntity<?> getOverview(){
        try{
            AdminStatisticsOverViewDTO overview = this.adminStatisticService.getOverview();
            
            return ResponseEntity.ok(overview);
            
        } catch (ResponseStatusException ex){
            return this.buildErrorResponse(ex);
        }
    }
    
    private ResponseEntity<?> buildErrorResponse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getStatusCode().toString(),
                "message", ex.getReason()
        ));
    }
    
    
    
    
}
