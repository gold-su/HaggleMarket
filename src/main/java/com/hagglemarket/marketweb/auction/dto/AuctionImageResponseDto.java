package com.hagglemarket.marketweb.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuctionImageResponseDto {
    private int count;              //이미지 총 개수
    private List<ImageInfo> images; //이미지 정보 리스트

    @Data
    @Builder    //빌더 패턴 생성자 제공
    @AllArgsConstructor //모든 필드를 받는 생성자 자동 생성
    public static class ImageInfo {
        private int imageId;        //이미지의 DB PK
        private String imageUrl;    //실제 접근 가능한 경로
        private String imageName;   //이미지 파일명
        private String imageType;   //이미지 타입
        private int sortOrder;      //이미지 노출 순서
        private long size;          //파일 크기
    }
}
