package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.post.entity.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostRequestDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private int categoryId;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int cost;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private Post.ProductStatus productStatus; // 예: NEW, USED

    private boolean negotiable;
    private boolean swapping;
    private boolean deliveryFee;

    private List<String> imageUrls = new ArrayList<>();
    private Post.Poststatus status;

    private String category;       // 소분류 이름
    private String tag;            // 태그 문자열 (#태그1,#태그2 등)
    private String tradeLocation;
}
