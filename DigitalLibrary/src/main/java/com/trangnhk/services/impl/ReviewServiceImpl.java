/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.CreateReviewRequestDTO;
import com.trangnhk.dto.ReviewResponseDTO;
import com.trangnhk.dto.UpdateReviewRequestDTO;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.Review;
import com.trangnhk.pojo.User;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.ReviewRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.ReviewService;
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
 * @author user
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private DocumentRepository documentRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public List<ReviewResponseDTO> getDocumentReviews(Long documentId, Map<String, String> params) {
        Document document = this.documentRepo.getDocumentById(documentId);

        if (document == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");
        }

        return this.reviewRepo.getDocumentReviews(documentId, params).stream().map(ReviewResponseDTO::fromReview).toList();
    }

    @Override
    public ReviewResponseDTO createReview(CreateReviewRequestDTO dto, String username) {
        Document document = this.documentRepo.getDocumentById(dto.getDocumentId());

        if (document == null || !Boolean.TRUE.equals(document.getApproved())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        if (dto.getComment() != null && dto.getComment().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Comment too long");
        }

        User user = this.userRepo.getUserByUsername(username);
        boolean reviewed = this.reviewRepo.existsByUserAndDocument(user.getId(), dto.getDocumentId());
        if (reviewed) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Review already exists");
        }

        // optional business rule
//        boolean borrowed = this.borrowRepo.hasBorrowedDocument( user.getId(),dto.getDocumentId());
//        if (!borrowed) {
//
//            throw new ResponseStatusException(
//                    HttpStatus.UNPROCESSABLE_ENTITY, "You must borrow document before review");
//        }
        Review review = new Review();
        review.setUser(user);
        review.setDocument(document);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedDate(new Date());
        this.reviewRepo.createReview(review);

        return ReviewResponseDTO.fromReview(review);
    }

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, UpdateReviewRequestDTO dto, String username) {
        Review review = this.reviewRepo.getReviewById(reviewId);

        if (review == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Review not found");
        }

        User user = this.userRepo.getUserByUsername(username);

        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals("ROLE_ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot edit this review");
        }

        if (dto.getRating() != null && (dto.getRating() < 1 || dto.getRating() > 5)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Rating must be between 1 and 5");
        }

        if (dto.getComment() != null && dto.getComment().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Comment too long");
        }

        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }

        if (dto.getComment() != null) {
            review.setComment(dto.getComment());
        }

        this.reviewRepo.updateReview(review);

        return ReviewResponseDTO.fromReview(review);
    }

    @Override
    public void deleteReview(Long reviewId, String username) {
        Review review = this.reviewRepo.getReviewById(reviewId);

        if (review == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Review not found");
        }
        User user = this.userRepo.getUserByUsername(username);

        boolean isOwner = review.getUser().getId().equals(user.getId());

        boolean isAdmin = user.getRole().equals("ROLE_ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You cannot delete this review");
        }

        this.reviewRepo.deleteReview(review);
    }

}
