package com.hagglemarket.marketweb.shop.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ShopStatsDto {
    private long totalProducts;    // 중고
    private long activeProducts;   // 중고-판매중
    private long soldProducts;     // 중고-판매완료
    private long totalAuctions;    // 경매
    private long activeAuctions;   // 경매-진행중
    private long soldAuctions;     // 경매-낙찰완료
    private long totalLikes;       // 내 상품 받은 찜수 합
    private long followers;
    private long following;
    private BigDecimal ratingAvg;
    private LocalDateTime storeOpenDate;
    private long reviewCount;
}