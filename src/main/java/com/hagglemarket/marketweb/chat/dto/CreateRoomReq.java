package com.hagglemarket.marketweb.chat.dto;

import com.hagglemarket.marketweb.chat.enums.RoomKind;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomReq { //채팅방 생성 요청 바디.
    @NotNull private RoomKind roomKind; //POST | AUCTION | ORDER // 방 종류에 따라 셋 중 하나만 채우고
    //상대방(판매자) PK인 sellerUserNo를 함께 보내는 구조.
    //리소스 식별자들. roomKind에 따라 정확히 하나만 채워야 함.
    private Integer postId;
    private Integer auctionId;
    private Integer orderId;

    //대화 상대(판매자) PK
    private Integer sellerUserNo;
}
