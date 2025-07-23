package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.post.entity.Post;
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
    private List<String> images;
    private SellerInfo seller;

    public static PostDetailResponse from(Post post, boolean isMine,List<String> imageUrls) {
        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .productStatus(post.getProductStatus().name())
                .cost(post.getCost())
                .negotiable(post.isNegotiable())
                .swapping(post.isSwapping())
                .deliveryFee(post.isDeliveryFee())
                .content(post.getContent())
                .hit(post.getHit())
                .createdAt(post.getCreatedAt())
                .status(post.getStatus().name())
                .isMine(isMine)
                .images(imageUrls)
                .seller(new SellerInfo(post.getUser()))
                .build();
    }

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
