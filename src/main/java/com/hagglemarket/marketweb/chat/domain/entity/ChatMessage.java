package com.hagglemarket.marketweb.chat.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity @Table(name = "chat_messages",
indexes = {  @Index(name="idx_room_created_pk", columnList="chat_room_id, created_at, chat_message_id"), //방+시간순 페이징과 pk 까지 고려
        @Index(name="idx_msg_room_id",     columnList="chat_room_id, chat_message_id") })   //
public class ChatMessage {
}
