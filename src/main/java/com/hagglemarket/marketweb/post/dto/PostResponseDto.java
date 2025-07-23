package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private int postId;
    private String title;
    private int cost;
    private String content;

    private String productStatus;
    private boolean negotiable;
    private boolean swapping;
    private boolean deliveryFee;

    private Post.Poststatus status;
    private int hit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> imageUrls;
}
