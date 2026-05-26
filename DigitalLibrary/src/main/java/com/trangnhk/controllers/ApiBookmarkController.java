/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.pojo.Bookmark;
import com.trangnhk.services.BookmarkService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/api/secure/bookmarks")
@Validated
public class ApiBookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyBookmarks(@RequestParam Map<String, String> params, Principal principal) {
        System.out.println(params);
        System.out.println(principal.getName());
        return ResponseEntity.ok(this.bookmarkService.getMyBookmarks(principal.getName(), params));
    }

    @PostMapping("/{documentId}")
    public ResponseEntity<?> addBookmark(@PathVariable("documentId") Long documentId, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.bookmarkService.addBookmark(documentId,principal.getName()));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteBookmark(@PathVariable("documentId") Long documentId, Principal principal) {

        this.bookmarkService.deleteBookmark(documentId, principal.getName());

        return ResponseEntity.noContent().build();
    }
}
