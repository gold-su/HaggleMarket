package com.hagglemarket.marketweb.postlike.dto;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeItemDto {
    private int id; // postId 또는 auctionId
    private String title;
    private String thumbnail;
    private int cost;
    private boolean isAuction; // 일반/경매 구분

    public static LikeItemDto fromPost(Post post) {
        String imageUrl = null;
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            imageUrl = "http://localhost:8080/uploads/" + post.getImages().get(0).getImageUrl();
        }
        return LikeItemDto.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .cost(post.getCost())
                .thumbnail(imageUrl)
                .isAuction(false)
                .build();
    }

    public static LikeItemDto fromAuction(AuctionPost auction) {
        String imageUrl = null;
        if (auction.getImages() != null && !auction.getImages().isEmpty()) {
            int imageId = auction.getImages().get(0).getImageId();
            imageUrl = "http://localhost:8080/api/auction/images/" + imageId;
        }

        return LikeItemDto.builder()
                .id(auction.getAuctionId())
                .title(auction.getTitle())
                .cost(auction.getStartCost())
                .thumbnail(imageUrl)
                .isAuction(true)
                .build();
    }
}
