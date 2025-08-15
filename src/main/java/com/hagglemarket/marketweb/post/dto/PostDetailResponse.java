package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.user.entity.User;
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
    public static class SellerInfo {
        private final int userNo;
        private final String nickName;
        private final String address;
        private final BigDecimal rating;
        private final String imageURL;

        public SellerInfo(User user) {
            this.userNo = user.getUserNo();
            this.nickName = user.getNickName();
            this.address = user.getAddress();
            this.rating = user.getRating();
            this.imageURL = user.getImageURL();
        }
    }
}
