/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.DocumentCompareResponseDTO;
import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.dto.ReviewResponseDTO;
import com.trangnhk.services.DocumentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.trangnhk.services.ReviewService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/documents")
public class ApiDocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<?> getDocuments(@RequestParam Map<String, String> params,
            @CookieValue(value = "document_sort", required = false) String documentSortCookie,
            @CookieValue(value = "document_size", required = false) String documentSizeCookie,
            HttpServletResponse response
    ) {
        applyDocumentCookiesToParams(params, documentSortCookie, documentSizeCookie);
        String validationError = this.documentService.validatePublicDocumentParams(params);

        if (validationError != null) {
            if (validationError.equals("CATEGORY_NOT_FOUND")) {
                return ResponseEntity.status(404).body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 404,
                        "error", "Not Found",
                        "message", "Can't found Category"
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 400,
                    "error", "Bad Request",
                    "message", validationError
            ));
        }

        saveDocumentPreferenceCookies(params, response);

        return ResponseEntity.ok(this.documentService.getPublicDocuments(params));

    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> getDocumentById(@PathVariable("documentId") Long documentId) {
        DocumentResponseDTO document = this.documentService.getPublicDocumentById(documentId);

        if (document == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Document not found"
            ));
        }

        return ResponseEntity.ok(document);
    }

    @GetMapping("/{documentId}/files")
    public ResponseEntity<?> getDocumentFiles(@PathVariable("documentId") Long documentId, HttpServletResponse response) {
        List<DocumentFileResponseDTO> files = this.documentService.getPublicDocumentFiles(documentId);

        if (files == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Document File not found"
            ));
        }

        saveLastViewedDocumentCookies(documentId, response);

        return ResponseEntity.ok(files);
    }

    private boolean isValidDocumentSort(String sort) {
        if (sort == null) {
            return false;
        }
        return sort.equals("newest")
                || sort.equals("title")
                || sort.equals("publishYear")
                || sort.equals("popular");
    }

    private void applyDocumentCookiesToParams(Map<String, String> params, String documentSortCookie, String documentSizeCookie) {
        if (!params.containsKey("sort") || params.get("sort") == null || params.get("sort").trim().isEmpty()) {

            if (documentSortCookie != null && !documentSortCookie.trim().isEmpty() && isValidDocumentSort(documentSortCookie.trim())) {
                params.put("sort", documentSortCookie);
            } else {
                params.put("sort", "newest");
            }
        }

        if (!params.containsKey("size") || params.get("size") == null || params.get("size").trim().isEmpty()) {

            if (documentSizeCookie != null && !documentSizeCookie.trim().isEmpty()) {
                params.put("size", documentSizeCookie);
            } else {
                params.put("size", "10");
            }
        }
    }

    private void saveDocumentPreferenceCookies(Map<String, String> params, HttpServletResponse response) {
        String sort = params.getOrDefault("sort", "newest");
        String size = params.getOrDefault("size", "10");

        Cookie sortCookie = new Cookie("document_sort", sort);
        sortCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        sortCookie.setPath("/");
        sortCookie.setHttpOnly(false);

        Cookie sizeCookie = new Cookie("document_size", size);
        sizeCookie.setMaxAge(7 * 24 * 60 * 60);
        sizeCookie.setPath("/");
        sizeCookie.setHttpOnly(false);

        response.addCookie(sortCookie);
        response.addCookie(sizeCookie);
    }

    private void saveLastViewedDocumentCookies(Long documentId, HttpServletResponse response) {
        String viewedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        Cookie documentIdCookie = new Cookie("last_viewed_document_id", documentId.toString());

        documentIdCookie.setMaxAge(24 * 60 * 60); // 1day
        documentIdCookie.setPath("/");
        documentIdCookie.setHttpOnly(false);

        response.addCookie(documentIdCookie);
    }

    @GetMapping("/{documentId}/reviews")
    public ResponseEntity<?> getDocumentReviews(@PathVariable("documentId") Long documentId, @RequestParam Map<String, String> params) {
        List<ReviewResponseDTO> reviews = this.reviewService.getDocumentReviews(documentId, params);
        if (reviews == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Document not found"
            ));
        }
        return ResponseEntity.ok(this.reviewService.getDocumentReviews(documentId, params));

    }

    @GetMapping("/{documentId}/compare")
    public ResponseEntity<DocumentCompareResponseDTO> compareDocuments(
            @PathVariable("documentId") Long documentId,
            @RequestParam(value = "with", required = false) String with,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {

        List<Long> compareIds = null;

        if (with != null && !with.isBlank()) {
            compareIds = Arrays.stream(with.split(",")).map(Long::parseLong).toList();
        }

        return ResponseEntity.ok(documentService.compareDocuments(documentId, compareIds, categoryId));
    }

}
