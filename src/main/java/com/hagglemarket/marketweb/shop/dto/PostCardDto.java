package com.hagglemarket.marketweb.shop.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PostCardDto {
    private String mode;           // "used" | "auction"
    private int id;                // posts.post_id | auction_posts.auction_id
    private String title;
    private int cost;              // used: cost, auction: current_cost
    private Integer likeCount;
    private String thumbnailUrl;   // 첫 이미지
    private LocalDateTime createdAt;
    private LocalDateTime endsAt;  // auction only (null for used)
    private String status;         // posts.status | auction_posts.status
    private Integer hit;
}