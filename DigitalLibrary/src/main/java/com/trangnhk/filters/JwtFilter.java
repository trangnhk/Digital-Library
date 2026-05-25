/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.filters;

import com.trangnhk.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Admin
 */
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailService;

    public JwtFilter(UserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();

        // Only protect API secure
        if (url.startsWith(request.getContextPath() + "/api/secure")) {
//            String authHeader = request.getHeader("Authorization");

            String token = getTokenFromRequest(request);

            if (token == null || token.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token");
            }

//            if (authHeader == null || !authHeader.startsWith("Bearer ")){
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization Header");
//                
//                return;
//            }
            try {
                // Get token
//                String token = authHeader.substring(7);
                String username = JWTUtils.validateTokenAndGetUsername(token);

                if (username == null) {
                    SecurityContextHolder.clearContext();

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");

                    return;
                }

                // Load detail
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                // Tạo authentication object
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("jwt_token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;

    }

}
