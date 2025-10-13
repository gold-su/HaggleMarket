package com.hagglemarket.marketweb.security;

import com.hagglemarket.marketweb.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(prePostEnabled = true)
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
                        // === 인증 필요한 구체 경로(먼저; ant 패턴 사용) ===
                        .requestMatchers(HttpMethod.GET,    "/api/products/likes/sidebar").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/products/*/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/products/*/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/*/like").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/auctions/*/like/me").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/auctions/*/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions/*/like").authenticated()

                        // 2) 공개 GET(목록/상세 등)
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",
                                "/api/products/detail/**",
                                "/api/auction/list",
                                "/api/auction/**",
                                "/api/auctions/**",
                                "/api/auction/images/**",
                                "/api/categories/**",
                                "/api/search",
                                "/api/auction/hot",
                                "/api/shops/*",
                                "/api/shops/*/stats",
                                "/api/shops/*/products"
                        ).permitAll()

                        // 3) 로그인/정적 등
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/login/**", "/api/users/signup").permitAll()
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/js/**", "/images/**").permitAll()

                        .requestMatchers("/error").permitAll()
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
