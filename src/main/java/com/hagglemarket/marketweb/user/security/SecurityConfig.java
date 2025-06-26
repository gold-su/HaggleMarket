package com.hagglemarket.marketweb.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//스프링 설정 클래스라는 의미
@Configuration
//spring security 설정을 위한 구성 클래스
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/user/**", "/css/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable()) // REST API 형태거나 csrf 문제시 꺼도 됨
//                .formLogin().disable();  // 시큐리티 기본 로그인 기능 비활성화
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //csrf 보안 기능 비활성화. rest api는 꺼도 무방함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/signup", "/api/users/login").permitAll() //특정 경로 로그인 없이 접근 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요 (로그인 or 토큰)
                );
        return http.build(); //설정을 SecurityFiterChain으로 빌드하여 반환
    }

    @Bean
    //테스트용 유저를 메모리 안에 임시로 등록하는 설정
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username("admin")
                .password(encoder.encode("1234"))
                .roles("USER") //역할(권한)은 USER
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    //비밀번호를 암호화하거나 암호화된 비밀번호를 비교할 때 사용
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //BCrypt를 가장 많이 사용
}

