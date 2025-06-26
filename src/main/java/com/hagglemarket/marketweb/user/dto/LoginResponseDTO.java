package com.hagglemarket.marketweb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String userId;
    private String token;
}
