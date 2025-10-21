package com.hagglemarket.marketweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                //리액트에서 부여된 포트를 설정함
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                //리액트에서 사용하게될 각각의 API들을 허용하는 함수
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ✅ 중고 이미지 (C:/uploads/)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/uploads/");

        // ✅ 경매 이미지 (프로젝트 내부)
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:upload/"); // ← 프로젝트 루트 경로 기준 상대경로
    }

}
