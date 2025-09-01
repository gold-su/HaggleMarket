package com.hagglemarket.marketweb.auction.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
//클라이언트가 경매 상품 등록 시 보낼 데이터 구조
public class AuctionPostRequest {
    private String title;
    private String content;
    private int startCost;
    private Integer buyoutCost;     //즉시구매가 (nullable)
    private Integer categoryId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    //userNo는 보통 로그인 정보로 서버에서 추출하니까 요청에는 안 넣음
}
