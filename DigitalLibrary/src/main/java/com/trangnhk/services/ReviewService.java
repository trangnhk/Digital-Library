/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.ReviewResponseDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface ReviewService {
    List<ReviewResponseDTO> getDocumentReviews(Long documentId, Map<String, String> params);
}
