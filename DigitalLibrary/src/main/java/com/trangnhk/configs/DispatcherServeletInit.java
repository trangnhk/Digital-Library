/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.configs;

import com.trangnhk.filters.JwtFilter;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 *
 * @author Admin
 */
public class DispatcherServeletInit extends AbstractAnnotationConfigDispatcherServletInitializer{
    static {
        Dotenv dotenv = Dotenv.configure()
            .directory("D:/Digital-Library/DigitalLibrary")
            .load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {
            ThymeleafConfigs.class,
            HibernateConfigs.class,
            SpringSecurityConfigs.class,
            ApiSecurityConfigs.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {
            WebAppContextConfigs.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(new MultipartConfigElement("/", 5000000, 15000000, 0));
    }

//    @Override
//    protected Filter[] getServletFilters() {
//        return new Filter[]{new JwtFilter()};
//    }
    
    
    
}
