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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ===== 공개 허용 경로 =====
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/login/**", "/api/users/signup").permitAll()

                        // ===== GET 허용 (비로그인 접근 가능) =====
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",
                                "/api/products/**",
                                "/api/products/detail/**",
                                "/api/auction/list",
                                "/api/auction/**",          // 단수형
                                "/api/auctions/**",         // ✅ 복수형 (경매 찜 포함)
                                "/api/auction/images/**",
                                "/api/categories/**",
                                "/api/likes/sidebar",
                                "/api/search",
                                "/api/auction/hot",
                                "/api/shops/*",
                                "/api/shops/*/stats",
                                "/api/shops/*/products"
                        ).permitAll()

                        // ===== 일반상품 찜 =====
                        .requestMatchers(HttpMethod.GET,    "/api/products/{postId}/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/products/{postId}/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/{postId}/like").authenticated()

                        // ===== 경매상품 찜 =====
                        .requestMatchers(HttpMethod.GET,    "/api/auctions/{auctionId}/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/auctions/{auctionId}/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions/{auctionId}/like").authenticated()

                        // ===== 기타 인증 필요 =====
                        .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/images").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/products/**").authenticated()

                        // ===== 디버깅용, 정적 리소스, OPTIONS =====
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/js/**", "/images/**").permitAll()

                        // ===== 나머지는 인증 필요 =====
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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
