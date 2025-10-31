package com.hagglemarket.marketweb.chat.domain.entity;

import com.hagglemarket.marketweb.chat.domain.id.ChatRoomMemberId;
import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="chat_room_members")
@Getter
@Setter
public class ChatRoomMember {
    @EmbeddedId private ChatRoomMemberId id;

    @MapsId("chatRoomId")                               //id.chatRoomId 값을 이 연관관계의 PK 에서 채움
    @ManyToOne(fetch = FetchType.LAZY)                  //다:1 (여러 멤버 -> 하나의 채팅방), 지연로딩
    @JoinColumn(name="chat_room_id", nullable=false)    //FK 컬럼명 지정 + NOT NULL
    private ChatRoom room;                              //소속 채팅방

    @MapsId("userNo")
    //id.userNo 값을 이 연관관계의 PK 에서 채움
    @ManyToOne(fetch = FetchType.LAZY)                  //다:1 (여러 멤버 -> 한 명의 유저), 지연로딩
    @JoinColumn(name="user_no", nullable=false)         //FK 컬럼명 지정 + NOT NULL
    private User user;                                  //해당 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //마지막 읽은 메시지는 읽을 수 있으므로 optional
    @JoinColumn(name="last_read_message_id")            //FK : 읽은 적 없으면 NULL
    private ChatMessage lastReadMessage;                //"마지막 읽은 메시지" 포인터
}
