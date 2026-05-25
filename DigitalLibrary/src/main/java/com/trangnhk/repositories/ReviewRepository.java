/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Review;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public interface ReviewRepository {
    List<Review> getDocumentReviews(Long documentId, Map<String, String> params);
    
}
