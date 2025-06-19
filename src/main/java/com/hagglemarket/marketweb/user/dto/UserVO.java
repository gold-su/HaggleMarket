package com.hagglemarket.marketweb.user.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private int userId;
    private String phoneNumber;
    private String nickName;
    private String address;
    private String email;
    private String imageURL;
    private LocalDateTime create;
    private String status;
    private BigDecimal rating;
    private BigDecimal roadRating;
}