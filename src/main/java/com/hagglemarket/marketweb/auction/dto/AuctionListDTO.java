package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter //getter 자동 생성
@Builder //빌더 형식으로 데이터 넣을 수 있는 애노테이션
public class AuctionListDTO {
    private int id;
    private String title;
    private String thumbnailUrl;
    private Integer currentPrice;
    private LocalDateTime endTime;
    private int hit;        //조회수
    private int bidCount;   //입찰 횟수
}
