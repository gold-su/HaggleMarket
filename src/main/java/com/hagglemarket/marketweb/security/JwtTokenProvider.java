package com.hagglemarket.marketweb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    //기존 필터와 동일한 시크릿 키 사용
    private String secret = "your-256-bit-or-longer-secret-key-goes-here";

    private Key key;
    private final UserDetailsService userDetailsService; // CustomUserDetailsService

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** ✅ JWT 유효성 검사 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** ✅ Claims 파싱 */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** ✅ username 추출 (subject) */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** ✅ Authentication 객체 생성 */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /** ✅ username만 꺼내는 단축용 (기존 호환 유지용) */
    public String getUsername(String token) {
        return getUsernameFromToken(token);
    }

    /** ✅ UserDetails 직접 반환 */
    public UserDetails getUserDetails(String token) {
        return userDetailsService.loadUserByUsername(getUsernameFromToken(token));
    }
}
