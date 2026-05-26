/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.BookmarkResponseDTO;
import com.trangnhk.pojo.Bookmark;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.User;
import com.trangnhk.repositories.BookmarkRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.BookmarkService;
import com.trangnhk.services.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author user
 */
@Service
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepo;

    @Autowired
    private DocumentRepository documentRepo;

    @Autowired
    private UserRepository userRepo;
    

    @Override
    public List<BookmarkResponseDTO> getMyBookmarks(String username, Map<String, String> params) {
        return this.bookmarkRepo.getMyBookmarks(username, params).stream().map(BookmarkResponseDTO::fromBookmark).toList();

    }

    @Override
    public BookmarkResponseDTO addBookmark(Long documentId, String username) {
        Document document = this.documentRepo.getDocumentById(documentId);

        if (document == null || document.getApproved() == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        User user = this.userRepo.getUserByUsername(username);
        Boolean exist = this.bookmarkRepo.existsByUserAndDocument(user.getId(), documentId);
        if (exist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Document already bookmarked");
        }

        Bookmark b = new Bookmark();
        b.setUser(user);
        b.setDocument(document);
        b.setCreatedDate(new Date());

        this.bookmarkRepo.addBookmark(b);

        return BookmarkResponseDTO.fromBookmark(b);
    }

    @Override
    public void deleteBookmark(Long documentId, String username) {
        User user = this.userRepo.getUserByUsername(username);

        Bookmark bookmark = this.bookmarkRepo.getByUserAndDocument( user.getId(),documentId);

        if (bookmark == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Bookmark not found");
        }

        this.bookmarkRepo.deleteBookmark(bookmark);
    }
}


