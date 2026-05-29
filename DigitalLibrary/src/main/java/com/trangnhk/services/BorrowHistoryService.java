/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.BorrowResponseDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface BorrowHistoryService {
    public List<BorrowResponseDTO> getMyBorrows(String username, Map<String, String> params);
}
