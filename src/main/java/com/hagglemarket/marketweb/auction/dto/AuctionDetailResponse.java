package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter //가져오기만 할거니까
@Builder //빌더 패턴으로 객체 생성 가능 (가독성과 안정성 향상)
public class AuctionDetailResponse {

    private int auctionId;
    private String title;
    private String content;
    private Integer categoryId;

    private int currentCost;
    private int buyoutCost;
    private LocalDateTime endTime;

    private String sellerNickname;
    private String winnerNickname;


}
