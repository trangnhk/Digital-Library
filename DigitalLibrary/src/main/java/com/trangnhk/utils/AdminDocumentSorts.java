/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class AdminDocumentSorts {
    public static final String DEFAULT_SORT = "newest";
    
    public static final Map<String, String> ADMIN_DOCUMENT_SORT;
    
    static{
        Map<String, String> sorts = new HashMap<>();
        
        sorts.put("title", "d.title ASC");
        sorts.put("publishYear", "d.publishYear DESC");
        sorts.put("popular", "d.totalViews DESC");
        sorts.put("newest", "d.createdDate DESC");
        
        sorts.put("pending", "d.approved ASC, d.createdDate DESC");
        
        sorts.put("approved", "d.approved DESC, d.createdDate DESC");
        
        ADMIN_DOCUMENT_SORT = Collections.unmodifiableMap(sorts);
    }
}
