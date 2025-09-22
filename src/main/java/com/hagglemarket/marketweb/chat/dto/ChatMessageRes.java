package com.hagglemarket.marketweb.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
//클라이언트에 내려줄 "채팅 메시지 한 건"의 응답 모델
//엔티티를 직접 노출하지 않고, 필요한 필드만 안전하게 전달.
public class ChatMessageRes {
    //메시지 PK
    private Integer id;
    //방 id
    private Integer roomId;
    //보낸 유저 번호
    private Integer senderNo;
    //본문
    private String content;
    //enum 문자열 (chat, image)
    private String type;
    //enum 문자열 (normal, deleted)
    private String status;
    //응답 json 직렬화 시 날짜 포맷 지정.
    //서버 타임존 기준으로 직렬화됨.
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    //엔티티 -> DTO 변환 정적 메서드.
    public static ChatMessageRes from(ChatMessage m) {
        return ChatMessageRes.builder()
                .id(m.getId())
                .roomId(m.getRoom().getId())
                .senderNo(m.getSender().getUserNo())
                .content(m.getContent())
                .type(m.getMsgType().name())
                .status(m.getStatus().name())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
