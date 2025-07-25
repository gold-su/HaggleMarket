package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionImageResponseDto {

    private int imageUrl;       //이미지 ID
    private String imageName;   //이미지 파일명
    private String imageType;   //이미지 타입
    private int sortOrder;      //이미지 정렬
}
