/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.BookmarkResponseDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface BookmarkService {
    List<BookmarkResponseDTO> getMyBookmarks(String username, Map<String, String> params);
    BookmarkResponseDTO addBookmark(Long documentId, String username);
    void deleteBookmark(Long documentId, String username);
}
