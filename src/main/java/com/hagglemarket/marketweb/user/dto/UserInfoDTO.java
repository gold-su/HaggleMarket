package com.hagglemarket.marketweb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data // getter, setter, toString, equals, hashCode 모두 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
public class UserInfoDTO {
    private String userId;
    private String userName;
    private String nickName;
    private String email;
    private String phoneNumber;
    private String address;
    private String imageURL;
}
