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

        registry.addResourceHandler("/uploads/posts/**")
                .addResourceLocations("file:///C:/uploads/posts/");

        //프로필 및 경매 이미지 (프로젝트 내부 uploads)
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:uploads/profile/");

        registry.addResourceHandler("/uploads/auction/**")
                .addResourceLocations("file:uploads/auction/");

        //혹시 몰라 전체 uploads 폴더 통합 등록 (보조용)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:///C:/uploads/",
                        "file:uploads/"
                );
    }

}
