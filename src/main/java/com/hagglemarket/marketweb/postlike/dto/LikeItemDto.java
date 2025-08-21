package com.hagglemarket.marketweb.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeItemDto {
    private Integer postId;
    private String title;
    private String thumbnailUrl;
}
