package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // ADD YOUR VERCEL DOMAIN HERE:
                .allowedOrigins(
                    "http://localhost:3000", 
                    "https://clips-stat-tracker-git-main-caitlyns-projects-4dbc852e.vercel.app"
                ) 
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}