package com.hagglemarket.marketweb.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SearchItemDto {
    private Integer id;
    private String  source;
    private String  title;
    private String  snippet;
    private Integer price;
    private LocalDateTime createdAt;
    private Integer hit;
    private Double  score;
    private String  thumbnailUrl;
    private Integer thumbnailId;
}
