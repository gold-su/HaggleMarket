package com.hagglemarket.marketweb.bid.dto;


import com.hagglemarket.marketweb.bid.entity.BidHistory;


//매퍼 = 한 객체를 다른 형태의 객체로 변환해주는 작은 유틸(혹은 레이어)임.
//이 매퍼는 JPA 엔티티 -> API 응답 DTO로 바꿀 때 씀.
//BidHistory 엔티티를 BidHistoryItemDTO로 변환한다.
//즉, DB 에서 가져온 영속 객체(엔티티)를 컨트롤러가 바로 내보내기 좋은 가벼운 응답 모양으로 바꿔줌
public class BidHistoryMapper {
    public static BidHistoryItemDTO toDto(BidHistory b){
        return BidHistoryItemDTO.builder()
                .bidId(b.getBidId())
                .bidderId(b.getBidder().getUserNo())        //User PK명 맞춰서 수정
                .bidderNickname(b.getBidder().getNickName())//필드명 맞춰서 수정
                .bidAmount(b.getBidAmount())
                .bidTime(b.getBidTime())
                .build();
    }
}
