package com.hagglemarket.marketweb.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LikeItemDto {
    private String targetType;     // "POST" | "AUCTION"
    private Integer targetId;      // postId or auctionId
    private String title;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}