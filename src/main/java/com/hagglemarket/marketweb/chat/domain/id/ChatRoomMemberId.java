package com.hagglemarket.marketweb.chat.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable //이 클래스가 '임베디드 키(복합키) 값 타입'임을 선언
public class ChatRoomMemberId implements Serializable { //JPA 규약상 ID 클래스는 Serializable 이어야 함 (캐시/전송/키 비교 등에 필요)

    @Column(name="chat_room_id")
    private Integer chatRoomId;     //FK 컬럼 매핑 : 채팅방 PK (컬럼명 chat_room_id에 매핑)
    @Column(name="user_no")
    private Integer userNo;         //FK 컬럼 매핑 : 유저 Pk (컬럼명 user_no에 매핑)

    public ChatRoomMemberId() {}    // JPA가 사용하기 위한 기본 생성자(반드시 필요, public/protected)
    public ChatRoomMemberId(Integer roomId, Integer userNo){this.chatRoomId=roomId; this.userNo=userNo;} //편의 생성자: 코드에서 바로 값 세팅해 만들 때 사용
}
