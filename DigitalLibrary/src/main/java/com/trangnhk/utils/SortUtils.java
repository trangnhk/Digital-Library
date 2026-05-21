/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.utils;

import java.util.Map;

/**
 *
 * @author Admin
 */
public class SortUtils {
    
    // Get sort from request
    public static String getSortKey(Map<String, String> params, String defaultSort) {
        if (params == null) {
            return defaultSort;
        }

        String sort = params.get("sort");

        if (sort == null || sort.trim().isEmpty()) {
            return defaultSort;
        }

        return sort.trim();
    }
    
    // check valid sort
    public static boolean isValidSort(Map<String, String> params, Map<String, String> allowedSorts, String defaultSort) {
        String sort = getSortKey(params, defaultSort);

        return allowedSorts.containsKey(sort);
    }

    // create ORDER BY from whitelist
    public static String buildOrderBy(Map<String, String> params, Map<String, String> allowedSorts, String defaultSort) {
        String sort = getSortKey(params, defaultSort);

        if (!allowedSorts.containsKey(sort)) {
            sort = defaultSort;
        }

        return " ORDER BY " + allowedSorts.get(sort);
    }
}
