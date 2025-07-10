package com.hagglemarket.marketweb.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // 256비트(32바이트) 이상 길이의 비밀키를 문자열로 준비
    private static final String SECRET_STRING = "your-256-bit-or-longer-secret-key-goes-here";

    // SecretKey 생성
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(String userid) {
        return Jwts.builder()
                .setSubject(userid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Key 타입 사용
                .compact();
    }

    public String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String validateAndExtractUserId(String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        }catch (Exception e){
            throw new RuntimeException("유효하지 않은 토큰입니다");
        }
    }
}