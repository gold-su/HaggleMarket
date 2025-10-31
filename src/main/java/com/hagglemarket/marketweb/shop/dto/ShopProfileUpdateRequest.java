package com.hagglemarket.marketweb.shop.dto;

import lombok.Getter;

@Getter
public class ShopProfileUpdateRequest {
    private String nickname;   // nullable: 변경 시에만 포함
    private String intro;      // nullable
    private String profileUrl; // nullable
}
