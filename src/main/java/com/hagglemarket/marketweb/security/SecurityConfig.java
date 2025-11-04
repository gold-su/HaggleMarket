package com.hagglemarket.marketweb.security;

import com.hagglemarket.marketweb.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔹 CORS 설정 허용
                .cors().and()
                // 🔹 CSRF 비활성화 (JWT 기반이므로)
                .csrf(csrf -> csrf.disable())
                // 🔹 세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 🔹 요청 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // ✅ WebSocket 허용
                        .requestMatchers("/ws/**").permitAll()

                        .requestMatchers("/api/chat/**").permitAll()

                        // ✅ 로그인 / 회원가입 허용
                        .requestMatchers(
                                "/users/login",
                                "/api/users/login",
                                "/api/users/login/**",
                                "/api/users/signup"
                        ).permitAll()

                        // ✅ 찜목록 사이드바는 비로그인도 접근 가능
                        .requestMatchers(HttpMethod.GET,
                                "/api/products/likes/sidebar",
                                "/api/auction/likes/sidebar"
                        ).permitAll()

                        // ✅ 공개 조회 가능 (상품, 경매, 상점)
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",
                                "/api/products/**",
                                "/api/products/detail/**",
                                "/api/auction/list",
                                "/api/auction/**",
                                "/api/auctions/**",
                                "/api/auction/images/**",
                                "/api/categories/**",
                                "/api/search",
                                "/api/auction/hot",
                                "/api/shops/**"
                        ).permitAll()

                        // ✅ 상점 소개글 수정은 로그인 필요
                        .requestMatchers(HttpMethod.PUT, "/api/shops/me/intro").authenticated()

                        // ✅ 업로드 리소스 및 AI 관련
                        .requestMatchers("/uploads/**", "/api/ai/**").permitAll()

                        // ✅ 찜 관련 (개별 like/me, 등록/삭제는 인증 필요)
                        .requestMatchers(HttpMethod.GET, "/api/products/{postId}/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/{postId}/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/{postId}/like").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/auctions/{auctionId}/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{auctionId}/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions/{auctionId}/like").authenticated()

                        // ✅ 상품 작성 / 수정은 로그인 필요
                        .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/images").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()

                        // ✅ 디버깅용 / 기본 정적 파일
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/js/**", "/images/**", "/css/**").permitAll()

                        // ✅ 나머지는 모두 인증 필요
                        .anyRequest().authenticated()
                )

                // 🔹 JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 🔹 폼 로그인 비활성화
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("https://hagglemarket.onrender.com");
        config.addAllowedOrigin("https://hagglemarket-front-react.onrender.com");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/uploads/**",
                "/api/auction/images/**",
                "/css/**",
                "/js/**",
                "/images/**"
        );
    }
}
