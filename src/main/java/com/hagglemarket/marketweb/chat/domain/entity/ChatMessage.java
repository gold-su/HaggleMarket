package com.hagglemarket.marketweb.chat.domain.entity;

import com.hagglemarket.marketweb.chat.enums.MessageStatus;
import com.hagglemarket.marketweb.chat.enums.MessageType;
import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name = "chat_messages",
indexes = {  @Index(name="idx_room_created_pk", columnList="chat_room_id, created_at, chat_message_id"), //방+시간순 페이징과 pk 까지 고려
        @Index(name="idx_msg_room_id",     columnList="chat_room_id, chat_message_id") })   //방+PK 기준 페이징 최적화
@Setter @Getter
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_message_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="chat_room_id", nullable=false)
    private ChatRoom room; //메시지가 속한 방(FK). LAZY라 room을 실제 접근할 때만 쿼리 나감. DB DDL 이 ON DELETE CASCADE면 방 삭제 시 메시지도 같이 삭제됨

    @ManyToOne(fetch = FetchType.LAZY, optional = true) @JoinColumn(name="sender_user_no")
    private User sender; //보낸 사람(FK). SYSTEM 메시지는 NULL 허용(= 발신자 없음).

    @Enumerated(EnumType.STRING) @Column(name="msg_type", nullable=false)
    private MessageType msgType = MessageType.CHAT; //CHAT | SYSTEM 구분(문자열 저장). enum 순서 변경에도 안전.

    @Lob @Column(name = "content", columnDefinition = "TEXT")
    private String content; //본문. @Lob -> MySQL 에선 LONGTEXT / TEXT 류로 매핑. 긴 메시지/시스템 JSON 문자열도 수용

    @Column(name="client_msg_id")
    private Long clientMsgId; //클라이언트 생성 ID(낙관적 UI/중복전송 방지/ACK 매칭). -> (개선) room + client_msg_id 유니크 잡으면 더 안전함.

    @Enumerated(EnumType.STRING) @Column(name="status", nullable=false)
    private MessageStatus status = MessageStatus.NORMAL; //NORMAL | DELETED 등. 소프트 삭제 시 UI 에서 "삭제된 메시지입니다" 표시.

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt; //생성 시각. 현재는 DB 기본값/트리거에 의존

}
