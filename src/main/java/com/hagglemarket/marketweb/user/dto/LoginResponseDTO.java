package com.hagglemarket.marketweb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String userId;
    private String token;
    private String nickname; //닉네임 표시위한 반환
}
