package com.hagglemarket.marketweb.websocket.service;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.dto.ChatMessageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatWebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    //특정 채팅방(roomId)의 모든 구독자에게 새 메시지를 전송
    public void broadcastChat(int roomId, ChatMessageRes message) {
        String destination = "/topic/chat.room." + roomId;
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    public String askToAiBot(String message) {
        try {
            String url = "http://localhost:8080/api/ai/faq";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("message", message);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return (String) response.getBody().get("answer");
        } catch (Exception e) {
            e.printStackTrace();
            return "현재 AI 답변을 가져올 수 없습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}
