package com.hagglemarket.marketweb.bid.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequestDTO {
    private int auctionId; //어떤 경매글에 입찰할 건지
    private int userNo;    //누가 입찰하는지 (회원번호)
    private int bidAmount; //얼마로 입찰할 건지
}
