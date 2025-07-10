package com.hagglemarket.marketweb.security;

import com.hagglemarket.marketweb.user.service.MarketUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//스프링 설정 클래스라는 의미
@Configuration
@RequiredArgsConstructor
//spring security 설정을 위한 구성 클래스
public class SecurityConfig {
    private final MarketUserDetailsService marketUserDetailsService;
    @Bean
    //로그인 필터처리하는 곳
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login").permitAll()
                        //api를 허용함
                        .requestMatchers("/api/users/login", "/api/users/login/**", "/api/users/register").permitAll()
                        //css등 파일들을 허용함
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return marketUserDetailsService;  // DB에서 사용자 조회하는 서비스 반환
    }

    @Bean
    //비밀번호를 암호화하거나 암호화된 비밀번호를 비교할 때 사용
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //BCrypt를 가장 많이 사용
}

