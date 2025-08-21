package com.hagglemarket.marketweb.post.dto;

import com.hagglemarket.marketweb.post.entity.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int cost;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    // 상품 상태 (예: LIKE_NEW, USED_GOOD 등)
    private Post.ProductStatus productStatus;

    // 가격 제안 허용 여부
    private boolean negotiable;

    // 교환 가능 여부
    private boolean swapping;

    // 배송비 포함 여부
    private boolean deliveryFee;

    // 이미지 URL 리스트 (수정 시 기존 + 새 이미지 포함)
    private List<String> imageUrls;

    // 게시글 상태 (예: 판매중, 예약중, 판매완료 등 — 필요에 따라 활용)
    private Post.Poststatus status;

    private int categoryId;
    private String tag;            // 태그 문자열
    private String tradeLocation;  // 거래 지역
}
