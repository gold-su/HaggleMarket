package com.hagglemarket.marketweb.postlike.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeItemDto {
    private Integer id;             // postId 또는 auctionId
    private String  title;
    private String  thumbnailUrl;   // ✅ 완성된 URL만 담는다 (/uploads/... 또는 /api/auction/images/{id})
    private Integer price;          // post.cost or auction.startCost (선택)
    private boolean isAuction;      // 경매 여부
}
