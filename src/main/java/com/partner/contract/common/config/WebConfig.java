package com.partner.contract.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${secret.cors.allowed-origins.local}")
    private String local;

    @Value("${secret.cors.allowed-origins.domain}")
    private String domain;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(local, domain)
                .allowedMethods("GET", "POST", "PATCH", "DELETE");
    }
}
