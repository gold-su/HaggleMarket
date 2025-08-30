package com.hagglemarket.marketweb.security;

import com.hagglemarket.marketweb.user.repository.UserRepository;
import com.hagglemarket.marketweb.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//스프링 설정 클래스라는 의미
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity //security 설정 클래스 지정
//spring security 설정을 위한 구성 클래스
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //cors 허용: 프론트(다른 Origin)에서 WebSocket/REST 접근 가능하게
                .cors()
                .and()
                //csrf: jwt 무상태라 전역 비활성도 ok.
                //안전하게 가려면 최소한 /ws/** (핸드셰이크)만 csrf 예외 처리도 가능
                .csrf(csrf -> csrf.disable())
                //세션 비활성 : JWT 사용 -> 세션 상태 없음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //허용 경로 설정
                .authorizeHttpRequests(auth -> auth
                        //websocket 핸드셰이크 경로 허용 (중요)
                        .requestMatchers("/ws/**").permitAll()
                        //로그인/회원가입/파일업로드/경매 공개 API 등 허용
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/login/**", "/api/users/signup").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",
                                "/api/products/**",
                                "/api/products/detail/**",
                                "/api/auction/list",
                                "/api/auction/**",          // 상세/리스트
                                "/api/auction/images/**",    // 이미지 바이트
                                "/api/categories/**",
                                "/api/likes/sidebar",
                                "api/search" //검색api

                        ).permitAll()
                        // 좋아요(B안): 모두 인증 필요
                        .requestMatchers(HttpMethod.GET,    "/api/products/{postId}/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/products/{postId}/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/{postId}/like").authenticated()

                        // 기타 인증 필요
                        .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/images").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/products/**").authenticated()

                        .requestMatchers("/api/auth/me").permitAll() // 디버깅용 공개
                        //OPTIONS 프리플라이트 허용 + 정적 리소스
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/js/**", "/images/**").permitAll()
                        //그 외는 인증 필요
                        .anyRequest().authenticated()
                )
                //jwt 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //폼 로그인 미사용
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //BCryptPasswordEncoder를 가장 많이 사용
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**");
    }

}

