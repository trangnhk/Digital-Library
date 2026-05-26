/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Bookmark;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface BookmarkRepository {
    List<Bookmark> getMyBookmarks(String username, Map<String, String> params);
    void addBookmark(Bookmark bookmark);
    boolean existsByUserAndDocument(Long userId,Long documentId);
    Bookmark getByUserAndDocument(Long userId, Long documentId);
    void deleteBookmark(Bookmark bookmark);
    
}
