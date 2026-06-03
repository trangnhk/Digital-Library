/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.CreateReviewRequestDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.ReviewResponseDTO;
import com.trangnhk.dto.UpdateReviewRequestDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface ReviewService {
    PageResponseDTO<ReviewResponseDTO> getDocumentReviews(Long documentId, Map<String, String> params);
    ReviewResponseDTO createReview(CreateReviewRequestDTO dto,String username);
    ReviewResponseDTO updateReview(Long reviewId, UpdateReviewRequestDTO dto, String username);
    void deleteReview(Long reviewId, String username);
}
