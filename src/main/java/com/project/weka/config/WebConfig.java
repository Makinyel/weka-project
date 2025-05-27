package com.project.weka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("*") // o "http://localhost:5173"
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
  }
}