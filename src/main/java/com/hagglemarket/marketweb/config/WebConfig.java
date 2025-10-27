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
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                // 실제 PC 경로에 맞게 수정
                .addResourceLocations("file:///C:/Users/pds02/IdeaProjects/HaggleMarket/uploads/");
    }



}
