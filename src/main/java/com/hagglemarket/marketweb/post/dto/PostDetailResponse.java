package com.hagglemarket.marketweb.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailResponse {
    private int postId;
    private String title;
    private String productStatus;
    private int cost;
    private boolean negotiable;
    private boolean swapping;
    private boolean deliveryFee;
    private String content;
    private int hit;
    private LocalDateTime createdAt;
    private String status;
    private boolean isMine;
    private Integer categoryId;
    private String categoryPath;
    private List<String> images;
    private SellerInfo seller;
    private boolean likedByMe;
    private int likeCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SellerInfo {
        private final int userNo;
        private final String nickname;
        private final String profileUrl;
        private final boolean verified;
        private final LocalDateTime storeOpenedAt;
        private final String address;
        private final BigDecimal rating;
    }
}
