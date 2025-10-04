package com.hagglemarket.marketweb.chat.dto;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Getter //모든 필드는 읽기 전용(getter만)
@Builder
public class ChatRoomRes { //생성된/조회된 채팅방 정보를 프론트로 내려줄 때 쓰는 읽기 전용 응답 모델

    //Builder로 생성 시점에 필요한 값만 채워 불변에 가깝게 사용
    private Integer roomId;
    private String roomKind;
    private Integer postId;
    private Integer auctionId;
    private Integer orderId;
    private Integer sellerUserNo;
    private Integer buyerUserNo;

    //정적 팩토리 메서드로 엔티티 -> DTO 변환 위치를 고정
    //유지보수 용이하게 매핑 로직을 한 곳에
    public static ChatRoomRes from(ChatRoom r) {
        return ChatRoomRes.builder()
                .roomId(r.getId())
                .roomKind(r.getRoomKind().name())
                .postId(r.getPostId())
                .auctionId(r.getAuctionId())
                .orderId(r.getOrderId())
                .sellerUserNo(r.getSeller().getUserNo())
                .buyerUserNo(r.getBuyer() == null ? null : r.getBuyer().getUserNo())
                .build();
    }
}
