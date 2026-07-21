package com.risosuit.DiegoGomezTagleGestionProductos.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${apiURL}")
    private String ip;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://" + ip + ":4200")
                .allowedMethods("GET", "POST", "PUT","PATCH" ,"DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
