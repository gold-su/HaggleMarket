package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class HotAuctionItemDTO {
    private int auctionId;
    private long bidCount;

    private String title;
    private String thumbnailUrl;
    private Integer currentPrice;
    private LocalDateTime endTime;
    private String status;
}
