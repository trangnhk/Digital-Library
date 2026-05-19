/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 *
 * @author Admin
 */
@Configuration
@EnableWebSecurity
@ComponentScan(
        basePackages = {
            "com.trangnhk.controllers",
            "com.trangnhk.repositories",
            "com.trangnhk.services",}
)
@Order(2)
public class SpringSecurityConfigs {

    @Autowired
    private UserDetailsService userDetailService;

    // Encode Password
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/", "/admin/**", "/process-login").csrf(c -> c.disable()).authorizeHttpRequests((request) -> request
                // ADMIN
                .requestMatchers("/", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/admin/login", "/process-login").permitAll()
                .anyRequest().permitAll()
        )
                .formLogin(form -> form.loginPage("/admin/login") // Đường dẫn tới trang đăng nhập // LOGIN -> ADMIN
                .loginProcessingUrl("/process-login") // Đường dẫn xử lý POST
                .defaultSuccessUrl("/admin", true) // Chuyển hướng khi thành công
                .failureHandler((request, response, exception) -> {

                    response.setContentType("text/plain;charset=UTF-8");

                    response.getWriter().println("LOGIN ERROR");
                    response.getWriter().println(exception.getClass().getName());
                    response.getWriter().println(exception.getMessage());
                }) // Chuyển hướng khi thất bại
                .permitAll()
                ).logout((logout) -> logout.logoutSuccessUrl("/admin/login").permitAll());

        return http.build();
    }

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dxfbpkmen",
                "api_key", "771652583444831",
                "api_secret", "EwZYOpA4n19unyDcRFEDBud6LBA",
                "secure", true
        ));

        return cloudinary;
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.setAllowedOrigins(List.of("http://localhost:3000/"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//        config.setExposedHeaders(List.of("Authorization"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return source;
//    }
}
