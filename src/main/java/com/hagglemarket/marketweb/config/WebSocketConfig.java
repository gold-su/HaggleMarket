package com.hagglemarket.marketweb.config;


import com.hagglemarket.marketweb.security.JwtChannelInterceptor;
import com.hagglemarket.marketweb.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration //스프링이 시작될 때 이 클래스를 읽어 WebSocket 관련 Bean을 등록하게 함.
@EnableWebSocketMessageBroker  //STOMP 기반 WebSocket 활성화, 내장 메시지 브로커 또는 외부 브로커와 연결 가능하게 함.
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { //아래에서 configureMessageBroker와 registerStompEndpoints 메서드를 재정의해서 세부 설정.

    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final JwtTokenProvider jwtTokenProvider;
    private final HandshakeInterceptor jwtHandshakeInterceptor;
    @Override
    //MessageBrokerRegistry 객체를 통해 메시지 경로 설계를 설정.
    //클라이언트가 메시지를 받을 경로, 서버에 메시지를 보낼 경로를 정함.
    public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry registry) {
        //구독 prefix  //enableSimpleBroker()는 스프링 내장 메모리 브로커를 켜는 메서드.
        registry.enableSimpleBroker("/topic", "/queue"); //topic은 브로드캐스트용 (여러 구독자에게 전송) / queue는 1:1 메시지용 (특정 사용자 개인 큐)
        //예: 서버가 /topic/auction.1로 보내면 auction.1을 구독한 모든 클라이언트가 받음.
        //클라이언트에서 메시지를 보낼 때 붙이는 prefix
        registry.setApplicationDestinationPrefixes("/app"); //클라이언트가 /app/... 로 메시지를 보내면, 서버의 @MessageMapping("...") 메서드로 매핑됨.
        //예: 클라가 /app/bid.place로 전송 → 서버에서 @MessageMapping("/bid.place") 처리.
    }

    @Override
    //브라우저/클라이언트가 처음 WebSocket 연결을 맺을 URL을 정의.
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        //클라이언트 WebSocket 연결 엔드포인트
//        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5173", "http://localhost:3000");
//        핸드셰이크 엔드포인트 등록 addEndpoint("/ws"): 클라이언트가 최초 연결 시 /ws로 접속. 예: ws://localhost:8080/ws
//        .setAllowedOrigins("*"): CORS 문제 방지를 위해 모든 도메인 허용.
//        운영환경에서는 * 대신 특정 도메인만 허용하는 것이 보안상 안전. 현재는 SockJS 폴백을 안 붙였음. → 붙이려면 .withSockJS() 추가.
        // ✅ Custom HandshakeHandler 추가

        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000")
                .addInterceptors(jwtHandshakeInterceptor) // ✅ 추가!
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(
                            org.springframework.http.server.ServerHttpRequest request,
                            org.springframework.web.socket.WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {

                        Object auth = attributes.get("auth");
                        if (auth instanceof Principal principal) {
                            System.out.println("[WS] ✅ Handshake principal attached: " + principal.getName());
                            return principal;
                        } else {
                            System.out.println("[WS] ⚠️ No principal in handshake attributes");
                            return null;
                        }
                    }
                });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        //  JwtChannelInterceptor 등록
        registration.interceptors(jwtChannelInterceptor); // ✅ 이미 @Component로 등록된 Bean 주입
    }
}
