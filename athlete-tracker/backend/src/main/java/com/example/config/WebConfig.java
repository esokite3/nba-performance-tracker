package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // IMPORTANT: Replace 'YOUR-VERCEL-FRONTEND-URL.vercel.app' 
        // with the actual URL of your deployed Vercel frontend!
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "https://clips-stat-tracker.vercel.app/") 
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}