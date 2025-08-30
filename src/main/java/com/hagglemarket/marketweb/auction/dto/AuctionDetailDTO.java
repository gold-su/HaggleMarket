package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

@Getter
@Builder
public class AuctionDetailDTO {
    private int auctionId;
    private String title;
    private String content;
    private Integer startPrice;
    private Integer currentPrice;
    private Integer buyoutPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer categoryId;         // 소분류 id
    private String categoryPath;        // "대 > 중 > 소"
    private List<Integer> categoryIds;  // [largeId, middleId, smallId]

    private List<String> imagesUrls; //이미지

    private String sellerNickname;
    private String winnerNickname;
    private int hit;        //조회수
    private int bidCount;   //입찰 횟수
}
