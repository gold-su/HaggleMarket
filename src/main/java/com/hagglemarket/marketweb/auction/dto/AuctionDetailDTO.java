package com.hagglemarket.marketweb.auction.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.time.LocalDate;

@Getter
@Builder
public class AuctionDetailDTO {
    private int Id;
    private String title;
    private String description;
    private Integer startPrice;
    private Integer currentPrice;
    private LocalDate startTim;
    private LocalDate endTime;
    private List<String> images; //이미지
}
