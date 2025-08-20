package com.hagglemarket.marketweb.bid.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequestDTO {
    private int auctionId;   // 경매글 ID
    private int bidAmount;   // 입찰가
    private Integer userNo;  // 입찰자(임시: STOMP에서 payload로 받음) - JWT붙일 땐 제거 가능
}
