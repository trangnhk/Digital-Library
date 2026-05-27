/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.configs;

import com.trangnhk.filters.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author Admin
 */
@Configuration
@Order(1)
public class ApiSecurityConfigs {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/**")
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth
                // Public API
                .requestMatchers(
                        "admin/login",
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/logout",
                        "/api/categories/**",
                        "/api/documents/**",
                        "/process-login"
                ).permitAll()
                //ADMIN
                .requestMatchers(
                        "/api/secure/admin/**"
                ).hasRole("ADMIN")
                // LIBRARIAN
                .requestMatchers(
                        "/api/secure/librarian/documents/{documentId}",
                        "/api/secure/librarian/documents/{documentId}/borrowers"
                ).hasAnyRole("LIBRARIAN", "ADMIN")
                .requestMatchers("/api/secure/librarian/**").hasRole("LIBRARIAN")
                // SECURE API
                .requestMatchers("/api/secure/**").authenticated()
                // Any request
                .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                                                       {
                                                       "status":401,
                                                       "message":"Unauthorized"
                                                       }
                                                       """);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                                                       {
                                                       "status":403,
                                                       "message":"Forbidden"
                                                       }
                                                       """);
                })
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public JwtFilter jwtFilter(UserDetailsService userDetailsService) {
        return new JwtFilter(userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Set-Cookie"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
