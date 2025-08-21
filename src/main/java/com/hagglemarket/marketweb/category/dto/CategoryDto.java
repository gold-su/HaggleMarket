package com.hagglemarket.marketweb.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Integer id;
    private String name;
    private Integer parentId;
}
