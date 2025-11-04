package com.hagglemarket.marketweb.chat.service.impl;

import com.hagglemarket.marketweb.ai.controller.AiChatController;
import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.dto.ChatMessageRes;
import com.hagglemarket.marketweb.chat.enums.MessageStatus;
import com.hagglemarket.marketweb.chat.enums.MessageType;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import com.hagglemarket.marketweb.chat.repository.ChatMessageRepository;
import com.hagglemarket.marketweb.chat.repository.ChatRoomRepository;
import com.hagglemarket.marketweb.chat.service.ChatMessageService;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import com.hagglemarket.marketweb.user.service.BotUserSupport;
import com.hagglemarket.marketweb.websocket.service.ChatWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
//채팅 메시지 전송과 메시지 페이지 조회를 담당하는 서비스.
//sendChat은 방/보낸사람 검증 -> 메시지 엔티티 생성/저장 -> 방의 updated_at 갱신까지 처리하며,
//클라이언트 멱등키(clientMsgId)가 유니크 제약에 걸리면 중복 전송으로 간주해 최신 메시지를 반환.
//getMessages는 beforeId 유무로 최신 페이지 또는 이전 더보기를 수행함.
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    //메시지/방/유저 접근 리포지토리 의존성
    private final ChatMessageRepository messageRepo;
    private final ChatRoomRepository roomRepo;
    private final UserRepository userRepo;
    private final BotUserSupport botUserSupport;
    private final AiChatController aiChatController;
    private final ChatWebSocketService chatWebSocketService;
    private final ApplicationEventPublisher eventPublisher;
//
//    @Override @Transactional
//    public ChatMessage sendChat(int roomId, int senderNo, String content, Long clientMsgId){
//        ChatRoom room = roomRepo.findById(roomId).orElseThrow(); //방이 없으면 예외
//        //참여자 검증은 프로젝트 정책에 맞게 추가
//        //새 메시지 엔티티 구성.
//        ChatMessage m = new ChatMessage();
//        m.setRoom(room);
//        m.setSender(userRepo.findById(senderNo).orElseThrow());
//        m.setMsgType(MessageType.CHAT);
//        m.setContent(content);
//        m.setClientMsgId(clientMsgId);
//        m.setStatus(MessageStatus.NORMAL);
//        //저장 성공 시 방의 updated_at을 네이티브 쿼리로 갱신(목록 정렬용).
//        //DataIntegrityViolationException(주로 멱등키 유니크 충돌) 발생 시 중복 전송으로 간주하고 방의 최신 메시지를 반환해 멱등 처리.
//        try{
//            ChatMessage saved = messageRepo.save(m);
//            messageRepo.touchRoomUpdatedAt(roomId);
//            //봇 채팅방이면 AI 응답 자동 생성
//            if (room.getRoomKind() == RoomKind.BOT){
//                try {
//                    ResponseEntity<Map<String, String>> aiResponse =
//                            aiChatController.faq(Map.of("message",content));
//                    System.out.println("[AI RESPONSE RAW]"+aiResponse);
//                    if (aiResponse.getBody() != null)
//                        System.out.println("[AI ANSWER] " + aiResponse.getBody().get("answer"));
//
//                    String botAnswer = aiResponse.getBody() != null ? aiResponse.getBody().get("answer") : null;
//                    if (botAnswer == null) botAnswer = "해글봇 응답을 불러오지 못했습니다.";
//
//                    ChatMessage botMsg = new ChatMessage();
//                    botMsg.setRoom(room);
//                    botMsg.setSender(botUserSupport.getOrCreateBotUser());
//                    botMsg.setMsgType(MessageType.CHAT);
//                    botMsg.setContent(botAnswer);
//                    botMsg.setStatus(MessageStatus.NORMAL);
//
//                    messageRepo.save(botMsg);
//                    messageRepo.touchRoomUpdatedAt(roomId);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    ChatMessage errorMsg = new ChatMessage();
//                    errorMsg.setRoom(room);
//                    errorMsg.setSender(botUserSupport.getOrCreateBotUser());
//                    errorMsg.setMsgType(MessageType.CHAT);
//                    errorMsg.setContent("해글봇 오류 : 잠시 후 다시 시도해주세요.");
//                    errorMsg.setStatus(MessageStatus.NORMAL);
//                    messageRepo.save(errorMsg);
//                }
//            }
//
//            return saved;
//        }catch(DataIntegrityViolationException e) {
//            //uq_client_dedup에 걸린 중복 전송이면 최신 것 반환
//            return messageRepo.findTop1ByRoom_IdOrderByIdDesc(roomId).orElseThrow();
//        }
//    }

//    @Override
//    @Transactional
//    public ChatMessage sendChat(int roomId, int senderNo, String content, Long clientMsgId) {
//        ChatRoom room = roomRepo.findById(roomId).orElseThrow();
//
//        ChatMessage userMsg = new ChatMessage();
//        userMsg.setRoom(room);
//        userMsg.setSender(userRepo.findById(senderNo).orElseThrow());
//        userMsg.setMsgType(MessageType.CHAT);
//        userMsg.setContent(content);
//        userMsg.setClientMsgId(clientMsgId);
//        userMsg.setStatus(MessageStatus.NORMAL);
//
//        try {
//            ChatMessage savedUserMsg = messageRepo.save(userMsg);
//            messageRepo.touchRoomUpdatedAt(roomId);
//
//            // ✅ 1️⃣ 사용자의 메시지는 즉시 반환 (프론트에 바로 표시)
//            // (broadcast는 WebSocketService에서 따로 수행됨)
//            if (room.getRoomKind() == RoomKind.BOT) {
//                // ✅ 2️⃣ AI 응답은 비동기로 따로 처리
//                CompletableFuture.runAsync(() -> {
//                    try {
//                        ResponseEntity<Map<String, String>> aiResponse =
//                                aiChatController.faq(Map.of("message", content));
//
//                        String botAnswer = (aiResponse.getBody() != null)
//                                ? aiResponse.getBody().get("answer")
//                                : "AI 응답을 불러올 수 없습니다.";
//
//                        ChatMessage botMsg = new ChatMessage();
//                        botMsg.setRoom(room);
//                        botMsg.setSender(botUserSupport.getOrCreateBotUser());
//                        botMsg.setMsgType(MessageType.CHAT);
//                        botMsg.setContent(botAnswer);
//                        botMsg.setStatus(MessageStatus.NORMAL);
//
//                        messageRepo.save(botMsg);
//                        messageRepo.touchRoomUpdatedAt(roomId);
//
//                        // ✅ 3️⃣ WebSocket 브로드캐스트
//                        chatWebSocketService.broadcastChat(roomId,
//                                ChatMessageRes.from(botMsg, senderNo));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        // AI 실패 시에도 클라이언트에 실시간 안내만 (DB 저장 X)
//                        ChatMessageRes errRes = ChatMessageRes.builder()
//                                .roomId(roomId)
//                                .content("현재 AI 답변을 가져올 수 없습니다. 잠시 후 다시 시도해주세요.")
//                                .type(MessageType.CHAT.name())
//                                .status(MessageStatus.NORMAL.name())
//                                .build();
//
//                        chatWebSocketService.broadcastChat(roomId, errRes);
//                    }
//                });
//            }
//
//            return savedUserMsg;
//
//        } catch (DataIntegrityViolationException e) {
//            return messageRepo.findTop1ByRoom_IdOrderByIdDesc(roomId).orElseThrow();
//        }
//    }
//
//
//    @Override
//    //메시지 페이지 조회
//    //beforeId가 null 이면 최신부터 size개.
//    //beforeId가 null 이 아니면 그 id 보다 작은 과거 size개
//    public Page<ChatMessage> getMessages(int roomId, Integer beforeId, int size){
//        return beforeId == null
//                ? messageRepo.findByRoom_IdOrderByIdDesc(roomId, PageRequest.of(0,size))
//                : messageRepo.findByRoom_IdAndIdLessThanOrderByIdDesc(roomId, beforeId, PageRequest.of(0, size));
//    }
    /**
     * 일반 채팅 메시지 저장 (BOT방일 경우 AI 응답 트리거 포함)
     */
    @Override
    @Transactional
    public ChatMessage sendChat(int roomId, int senderNo, String content, Long clientMsgId) {
        ChatRoom room = roomRepo.findById(roomId).orElseThrow();

        ChatMessage userMsg = new ChatMessage();
        userMsg.setRoom(room);
        userMsg.setSender(userRepo.findById(senderNo).orElseThrow());
        userMsg.setMsgType(MessageType.CHAT);
        userMsg.setContent(content);
        userMsg.setClientMsgId(clientMsgId);
        userMsg.setStatus(MessageStatus.NORMAL);

        try {
            ChatMessage savedUserMsg = messageRepo.save(userMsg);
            messageRepo.touchRoomUpdatedAt(roomId);

            //  봇 방이면 비동기로 AI 응답 트리거 (실시간 유지)
            if (room.getRoomKind() == RoomKind.BOT) {
                triggerAsyncAiResponse(roomId, senderNo, content);
            }

            return savedUserMsg;

        } catch (DataIntegrityViolationException e) {
            // 멱등 처리: 동일 clientMsgId 재전송 시 마지막 메시지 반환
            return messageRepo.findTop1ByRoom_IdOrderByIdDesc(roomId).orElseThrow();
        }
    }

    /**
     * ✅ 비동기 컨텍스트(@Async)에서 실행되어 WebSocket 세션 유실 없이 broadcast 가능
     * 트랜잭션 종료 후 별도 스레드에서 AI 응답 생성 및 전송 수행
     */
    @Async
    public void triggerAsyncAiResponse(int roomId, int senderNo, String content) {
        try {
            ChatRoom roomEntity = roomRepo.findById(roomId).orElseThrow();

            // 🔹 OpenAI API 호출
            ResponseEntity<Map<String, String>> aiResponse =
                    aiChatController.faq(Map.of("message", content));

            String botAnswer = (aiResponse.getBody() != null)
                    ? aiResponse.getBody().get("answer")
                    : "AI 응답을 불러올 수 없습니다.";

            // 🔹 AI 메시지 저장
            ChatMessage botMsg = new ChatMessage();
            botMsg.setRoom(roomEntity);
            botMsg.setSender(botUserSupport.getOrCreateBotUser());
            botMsg.setMsgType(MessageType.CHAT);
            botMsg.setContent(botAnswer);
            botMsg.setStatus(MessageStatus.NORMAL);

            messageRepo.save(botMsg);
            messageRepo.touchRoomUpdatedAt(roomId);

            // 🔹 실시간 broadcast (프론트 새로고침 없이 반영)
            chatWebSocketService.broadcastChat(roomId,
                    ChatMessageRes.from(botMsg, senderNo));

            System.out.println("[BOT]  AI 응답 실시간 전송 완료");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[BOT]  AI 응답 처리 중 오류 발생");
        }
    }

    /**
     * 메시지 페이지 조회 (최신 또는 이전 더보기)
     */
    @Override
    public Page<ChatMessage> getMessages(int roomId, Integer beforeId, int size) {
        return beforeId == null
                ? messageRepo.findByRoom_IdOrderByIdDesc(roomId, PageRequest.of(0, size))
                : messageRepo.findByRoom_IdAndIdLessThanOrderByIdDesc(roomId, beforeId, PageRequest.of(0, size));
    }
}
