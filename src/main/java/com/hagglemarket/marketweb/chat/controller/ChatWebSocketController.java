package com.hagglemarket.marketweb.chat.controller;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.dto.ChatMessageRes;
import com.hagglemarket.marketweb.chat.dto.SendMessageReq;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import com.hagglemarket.marketweb.chat.service.ChatMessageService;
import com.hagglemarket.marketweb.common.BotReplyGuard;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import com.hagglemarket.marketweb.user.service.BotUserSupport;
import com.hagglemarket.marketweb.websocket.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    //DB에 메시지 저장하는 역할
    private final ChatMessageService chatMessageService;
    //SimpMessagingTemplate로 브로드캐스트
    private final ChatWebSocketService chatWebSocketService;
    //챗봇용 가짜 계정 생성/조회
    private final BotUserSupport botUserSupport;
    //챗봇 중복 응답 방지용 락 유틸
    private final BotReplyGuard botReplyGuard;

    //메시지 수싲 Entry Point
    @MessageMapping("/chat.send.{roomId}")
    public void sendChat(@DestinationVariable int roomId, //DestinationVariable는 STOMP 프로토콜의 메시지 헤더에 있는 경로 변수를 메서드 파라미터에 바인딩하는데 사용
                         SendMessageReq req,
                         Principal principal) {
        //1) 인증 확인  principal(주요한)
        if (principal == null) {
            throw new IllegalStateException("WebSocket 인증 정보가 없습니다 (principal null)");
        }

        // 2) Principal -> Authentication 변환
        // JwtChannelInterceptor가 CONNECT 단계에서 setUser(authentication) 했기 때문에
        // 여기서 Principal이 실제 인증 객체로 들어옴.
        UsernamePasswordAuthenticationToken authentication =    //UsernamePasswordAuthenticationToken은 스프링 시큐리티에서 "아이디/비밀번호 기반 인증 상태"를 담는 토큰 객체
                (UsernamePasswordAuthenticationToken) principal;

        // 3) 유저 정보 추출
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        int senderNo = user.getUserNo();

        System.out.println("[WS][SEND] user=" + user.getUsername() + " roomId=" + roomId);

        // 사용자 메시지 저장 (DB)
        ChatMessage saved = chatMessageService.sendChat(
                roomId, senderNo, req.getContent(), req.getClientMsgId());

        // 방 구독자에게 브로드캐스트
        //현재 방을 구독 중인 모든 클라이언트가 즉시 수신
        ChatMessageRes response = ChatMessageRes.from(saved, senderNo);
        chatWebSocketService.broadcastChat(roomId, response);

//        // AI 챗봇 방이면 AI 답변 트리거
//        if (saved.getRoom().getRoomKind() == RoomKind.BOT) {
//            int botNo = botUserSupport.getOrCreateBotUser().getUserNo();
//
////            // AI 답변 가져오기
////            String aiAnswer = chatWebSocketService.askToAiBot(req.getContent());
////
////            // AI 메시지 저장
////            ChatMessage botReply = chatMessageService.sendChat(roomId, botNo, aiAnswer, null);
////
////            // AI 메시지도 실시간 전송
////            ChatMessageRes botResponse = ChatMessageRes.from(botReply, senderNo);
////            chatWebSocketService.broadcastChat(roomId, botResponse);
////
////            System.out.println("[BOT] AI 응답 실시간 전송 완료");
////            // 중복 실행 방지용 락 (메시지 단위)
////            String key = "BOT_LOCK_" + saved.getRoom().getId() + "_" + saved.getId();
////            if (botReplyGuard.tryLock(key, 5)) { // 5초 내 중복 방지
////                new Thread(() -> {
////                    try {
////                        String aiAnswer = chatWebSocketService.askToAiBot(req.getContent());
////                        ChatMessage botReply = chatMessageService.sendChat(roomId, botNo, aiAnswer, null);
////                        ChatMessageRes botResponse = ChatMessageRes.from(botReply, senderNo);
////                        chatWebSocketService.broadcastChat(roomId, botResponse);
////                        System.out.println("[BOT] AI 응답 실시간 전송 완료");
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    } finally {
////                        botReplyGuard.unlock(key);
////                    }
////                }).start();
////            }
//            //중복 응답 방지용 락
//            //동일한 방(roomId) + 동일한 clientMsgId 조합에 대해
//            //5초 이내에는 AI가 중복 응답하지 않도록 막는다.
//            String key = "BOT_LOCK_" + roomId + "_" + req.getClientMsgId();
//            if (botReplyGuard.tryLock(key, 5)) {
//                //새 thread 에서 AI 호출 (비동기)
//                new Thread(() -> {
//                    try {
//                        //AI API 호출
//                        String aiAnswer = chatWebSocketService.askToAiBot(req.getContent());
//                        //AI 답변 메시지를 DB에 저장
//                        ChatMessage botReply = chatMessageService.sendChat(roomId, botNo, aiAnswer, null);
//                        //AI 답변을 구독자에게 브로드캐스트
//                        ChatMessageRes botResponse = ChatMessageRes.from(botReply, senderNo);
//                        chatWebSocketService.broadcastChat(roomId, botResponse);
//
//                        System.out.println("[BOT] AI 응답 실시간 전송 완료");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        //락 해제
//                        botReplyGuard.unlock(key);
//                    }
//                }).start();
//            } else {
//                System.out.println("[BOT] 중복 호출 방지됨 (key=" + key + ")");
//            }
//        }
    }
}
