package com.hagglemarket.marketweb.user.security;

import com.hagglemarket.marketweb.user.repository.UserRepository;
import com.hagglemarket.marketweb.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//스프링 설정 클래스라는 의미
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity //security 설정 클래스 지정
//spring security 설정을 위한 구성 클래스
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    //로그인 필터처리하는 곳
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login").permitAll()
                        //api를 허용함
                        .requestMatchers("/api/users/login", "/api/users/login/**", "/api/users/signup").permitAll()
                        //css등 파일들을 허용함
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService()) //DB 연동된 서비스 사용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable();
        return http.build();
    }

//    @Bean
//    //테스트용 유저를 메모리 안에 임시로 등록하는 설정
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails user = User.builder()
//                .username("admin")
//                .password(encoder.encode("1234"))
//                .roles("USER") //역할(권한)은 USER
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository); // DB 연동 서비스로 변경
    }


    @Bean
    //비밀번호를 암호화하거나 암호화된 비밀번호를 비교할 때 사용
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //BCrypt를 가장 많이 사용
}

