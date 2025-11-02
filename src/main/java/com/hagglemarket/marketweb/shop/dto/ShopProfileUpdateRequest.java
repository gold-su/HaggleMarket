package com.hagglemarket.marketweb.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopProfileUpdateRequest {
    private String nickname;   // nullable: 변경 시에만 포함
    private String intro;      // nullable
    private String profileUrl; // nullable
}
