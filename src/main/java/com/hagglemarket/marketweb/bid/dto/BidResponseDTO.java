package com.hagglemarket.marketweb.bid.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder  //서비스 로직에서 깔끔하게 응답 객체 조립 가능
public class BidResponseDTO {
    private boolean success; //입찰 성공 여부
    private String message;  //성공/실패 메시지
    private Integer currentHighestBid; //현재 최고 입찰가
}
