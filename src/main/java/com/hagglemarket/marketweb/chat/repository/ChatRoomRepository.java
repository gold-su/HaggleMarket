package com.hagglemarket.marketweb.chat.repository;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    Optional<ChatRoom> findByRoomKindAndPostIdAndSeller_UserNoAndBuyer_UserNo(RoomKind k, Integer postId, Integer s, Integer b);
    Optional<>
}
