package com.example.aiproxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.*;
import java.time.Clock;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.cors.allowed-origin:http://localhost:3000}") private String allowedOrigin;
    @Bean public Clock systemClock() { return Clock.systemDefaultZone(); }
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(allowedOrigin).allowedMethods("GET", "POST", "OPTIONS").allowedHeaders("Content-Type", "X-User-Id");
    }
}
