package com.hagglemarket.marketweb.chat.service.impl;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.enums.MessageStatus;
import com.hagglemarket.marketweb.chat.enums.MessageType;
import com.hagglemarket.marketweb.chat.repository.ChatMessageRepository;
import com.hagglemarket.marketweb.chat.repository.ChatRoomRepository;
import com.hagglemarket.marketweb.chat.service.ChatMessageService;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override @Transactional
    public ChatMessage sendChat(int roomId, int senderNo, String content, Long clientMsgId){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(); //방이 없으면 예외
        //참여자 검증은 프로젝트 정책에 맞게 추가
        //새 메시지 엔티티 구성.
        ChatMessage m = new ChatMessage();
        m.setRoom(room);
        m.setSender(userRepo.findById(senderNo).orElseThrow());
        m.setMsgType(MessageType.CHAT);
        m.setContent(content);
        m.setClientMsgId(clientMsgId);
        m.setStatus(MessageStatus.NORMAL);
        //저장 성공 시 방의 updated_at을 네이티브 쿼리로 갱신(목록 정렬용).
        //DataIntegrityViolationException(주로 멱등키 유니크 충돌) 발생 시 중복 전송으로 간주하고 방의 최신 메시지를 반환해 멱등 처리.
        try{
            ChatMessage saved = messageRepo.save(m);
            messageRepo.touchRoomUpdatedAt(roomId);
            return saved;
        }catch(DataIntegrityViolationException e) {
            //uq_client_dedup에 걸린 중복 전송이면 최신 것 반환
            return messageRepo.findTop1ByRoom_IdOrderByIdDesc(roomId).orElseThrow();
        }
    }

    @Override
    //메시지 페이지 조회
    //beforeId가 null 이면 최신부터 size개.
    //beforeId가 null 이 아니면 그 id 보다 작은 과거 size개
    public Page<ChatMessage> getMessage(int roomId, Integer beforeId, int size){
        return beforeId == null
                ? messageRepo.findByRoom_IdOrderByIdDesc(roomId, PageRequest.of(0,size))
                : messageRepo.findByRoom_IdAndIdLessThanOrderByIdDesc(roomId, beforeId, PageRequest.of(0, size));
    }
}
