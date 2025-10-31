package com.hagglemarket.marketweb.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 접근자 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor == null) return message;

//        // 1. CONNECT 단계에서 JWT 인증
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String auth = accessor.getFirstNativeHeader("Authorization");
//            if (auth == null) auth = accessor.getFirstNativeHeader("authorization");
//
//            if (auth != null && auth.startsWith("Bearer ")) {
//                String token = auth.substring(7);
//                if (jwtTokenProvider.validateToken(token)) {
//                    var userDetails = jwtTokenProvider.getUserDetails(token);
//                    var authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//
//                    // 사용자 인증 정보 세팅
//                    accessor.setUser(authentication);
//                    accessor.getSessionAttributes().put("auth", authentication);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    System.out.println("[WS] CONNECT Authenticated user=" + userDetails.getUsername());
//                } else {
//                    System.out.println("[WS] Invalid JWT in CONNECT");
//                }
//            } else {
//                System.out.println("[WS] No Authorization header in CONNECT");
//            }
//        }
//        // 2. SEND / SUBSCRIBE 단계에서 세션 복원
//        else {
//            Authentication currentAuth = null;
//
//            if (accessor.getUser() instanceof Authentication auth)
//                currentAuth = auth;
//
//            if (currentAuth == null) {
//                currentAuth = (Authentication) accessor.getSessionAttributes().get("auth");
//                if (currentAuth != null) {
//                    accessor.setUser(currentAuth);
//                    SecurityContextHolder.getContext().setAuthentication(currentAuth);
//                    System.out.println("[WS] Restored Principal from session for " + currentAuth.getName());
//                } else {
//                    System.out.println("[WS] SEND without principal (still null)");
//                }
//            }
//        }
        // ✅ 1. CONNECT 단계에서 handshake에서 저장된 인증 복원
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var auth = (Authentication) accessor.getSessionAttributes().get("auth");
            if (auth != null) {
                accessor.setUser(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("[WS][CONNECT] Saved Principal to session for " + auth.getName());
            } else {
                System.out.println("[WS][CONNECT] No auth found in session");
            }
        }

        // ✅ 2. SEND / SUBSCRIBE 단계에서 세션에서 인증 복원
        else {
            if (accessor.getUser() == null) {
                var auth = (Authentication) accessor.getSessionAttributes().get("auth");
                if (auth != null) {
                    accessor.setUser(auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("[WS] Restored Principal from session for " + auth.getName());
                } else {
                    System.out.println("[WS] No Principal found for frame " + accessor.getCommand());
                }
            }
        }

        // ✅ 3. 메시지 재생성 (user 포함)
        return MessageBuilder.createMessage(
                message.getPayload(),
                accessor.getMessageHeaders()
        );
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // 스레드 재사용 방지
        SecurityContextHolder.clearContext();
    }
}
