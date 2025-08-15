package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostCardDto {
    private int postId;
    private String title;
    private int cost;
    private String thumbnail;
    private Post.ProductStatus status;
    private boolean liked;
    private List<String> tags;
    private boolean likedByMe;
    private int likeCount;
}
