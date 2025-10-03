package com.hagglemarket.marketweb.shop.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ShopProfileDto {
    private int userNo;
    private String nickname;
    private String intro;
    private String profileUrl;
    private boolean verified;
}
