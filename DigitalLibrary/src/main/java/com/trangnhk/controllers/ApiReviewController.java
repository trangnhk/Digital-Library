/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.CreateReviewRequestDTO;
import com.trangnhk.dto.ReviewResponseDTO;
import com.trangnhk.dto.UpdateReviewRequestDTO;
import com.trangnhk.services.ReviewService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/api/secure/reviews")
public class ApiReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequestDTO dto, Principal principal) {
        ReviewResponseDTO review = this.reviewService.createReview(dto, principal.getName());

        return ResponseEntity.status(201).body(review);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody UpdateReviewRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(this.reviewService.updateReview(reviewId, dto, principal.getName()));
    }

    @DeleteMapping("/{reviewId")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Principal principal) {
        this.reviewService.deleteReview(reviewId, principal.getName());

        return ResponseEntity.noContent().build();
    }
}
