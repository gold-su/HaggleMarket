package com.hagglemarket.marketweb.chat.dto;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomRes {

    private Integer roomId;
    private String roomKind;
    private Integer postId;
    private Integer auctionId;
    private Integer orderId;
    private Integer sellerUserNo;
    private Integer buyerUserNo;
    private String status;
    private LocalDateTime updateTime;

    private String otherUserName;
    private String otherUserProfileImageUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    //  1) meUserNo 포함 버전 (상대방 판단)
    public static ChatRoomRes from(ChatRoom r, Integer meUserNo, ChatMessage latest) {
        User other = r.getSeller().getUserNo() == meUserNo
                ? r.getBuyer()
                : r.getSeller();

        return ChatRoomRes.builder()
                .roomId(r.getId())
                .roomKind(r.getRoomKind().name())
                .postId(r.getPostId())
                .auctionId(r.getAuctionId())
                .orderId(r.getOrderId())
                .sellerUserNo(r.getSeller().getUserNo())
                .buyerUserNo(r.getBuyer() == null ? null : r.getBuyer().getUserNo())
                .status(r.getStatus().name())
                .updateTime(r.getUpdatedAt())

                // 상대방 정보
                .otherUserName(other != null ? other.getNickName() : "(탈퇴한 사용자)")
                .otherUserProfileImageUrl(other != null ? other.getImageURL() : null)

                // 최신 메시지
                .lastMessage(latest != null ? latest.getContent() : "")
                .lastMessageTime(latest != null ? latest.getCreatedAt() : null)

                .build();
    }


    //  2) 단일 인자 버전 (생성 시)
    public static ChatRoomRes from(ChatRoom r) {
        return ChatRoomRes.builder()
                .roomId(r.getId())
                .roomKind(r.getRoomKind().name())
                .postId(r.getPostId())
                .auctionId(r.getAuctionId())
                .orderId(r.getOrderId())
                .sellerUserNo(r.getSeller() != null ? r.getSeller().getUserNo() : null)
                .buyerUserNo(r.getBuyer() != null ? r.getBuyer().getUserNo() : null)
                .status(r.getStatus().name())
                .updateTime(r.getUpdatedAt())
                .build();
    }
}
