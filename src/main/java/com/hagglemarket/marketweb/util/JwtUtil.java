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
    //JWT에서 userId 추출
    public String extractUserId(String token) {
        Claims claims = Jwts.parser() //JWT 파서를 생성 "암호화된 봉투를 열 준비"
                .setSigningKey(SECRET_KEY) //JWT를 암호화할 때 사용했던 비밀키를 지정 "진짜 키인지 아닌지 확인할 때 필요한 열쇠"
                .parseClaimsJws(token) //JWT 토큰을 파싱해서 서명 검증까리 처리 "봉투가 손상되거나 위조되면 열리지 않음"
                .getBody(); //토크읜 payload(내용)을 꺼냄 '로그인 시에 넣어둔 정보들이 들어있음 (userId, 만료시간, roles 등)

        return claims.getSubject(); //JWT의 subject 값을 꺼내 반환 'subject는 로그인할 때 서버가 토큰에 넣어둔 사용자 식별자(userId)  임
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
