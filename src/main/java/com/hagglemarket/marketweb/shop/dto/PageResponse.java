package com.hagglemarket.marketweb.shop.dto;

import lombok.*;
import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}