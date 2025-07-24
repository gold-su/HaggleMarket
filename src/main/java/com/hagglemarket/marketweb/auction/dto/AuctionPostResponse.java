package com.hagglemarket.marketweb.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
//경매 상품 등록 후 클라이언트에 반환할 응답 DTO
public class AuctionPostResponse {

    private int auctionId;
    private String message;

}
