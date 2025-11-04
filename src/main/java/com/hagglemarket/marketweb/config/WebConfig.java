package com.hagglemarket.marketweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //리액트에서 부여된 포트를 설정함
                .allowedOrigins("http://localhost:5173", "http://localhost:3000","https://hagglemarket-front-react.onrender.com")
                //리액트에서 사용하게될 각각의 API들을 허용하는 함수
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/uploads/**")
//                // 실제 PC 경로에 맞게 수정
//                .addResourceLocations("file:///C:/Users/pds02/IdeaProjects/HaggleMarket/uploads/");
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ✅ 중고 이미지 (C 드라이브)
        registry.addResourceHandler("/uploads/posts/**") // ★ 하위 경로 포함
                .addResourceLocations("file:///C:/uploads/posts/");

        // ✅ 프로필 이미지 (프로젝트 내부)
        registry.addResourceHandler("/uploads/profile/**") // ★ 하위 경로 포함
                .addResourceLocations("file:uploads/profile/");

        // ✅ 경매 이미지 (프로젝트 내부)
        registry.addResourceHandler("/uploads/auction/**") // ★ 하위 경로 포함
                .addResourceLocations("file:uploads/auction/");

        // ✅ 전체 uploads 폴더 (보조용)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:///C:/uploads/",
                        "file:uploads/"
                );
    }

}
