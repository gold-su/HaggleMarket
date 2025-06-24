package com.hagglemarket.marketweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class config {

    @Bean
    //리액트 서버랑 연동할 때 필요한 CORS 설정
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 프론트에서 접근할 API 경로
                        .allowedOrigins("http://localhost:3000") // React 개발 서버 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true); // 인증 정보 포함 허용 (필요한 경우)
            }
        };
    }
}
