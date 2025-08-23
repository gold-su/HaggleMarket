package com.hagglemarket.marketweb.bid.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BidHistoryItemDTO {
    private int bidId;
    private int bidderId;
    private String bidderNickname;
    private Integer bidAmount;
    private LocalDateTime bidTime;
}
