/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.ReviewResponseDTO;
import com.trangnhk.pojo.Document;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.ReviewRepository;
import com.trangnhk.services.ReviewService;
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

    @Override
    public List<ReviewResponseDTO> getDocumentReviews(Long documentId, Map<String, String> params) {
        Document document = this.documentRepo.getDocumentById(documentId);

        if (document == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");
        }

        return this.reviewRepo.getDocumentReviews(documentId, params).stream().map(ReviewResponseDTO::fromReview).toList();
    }
}

