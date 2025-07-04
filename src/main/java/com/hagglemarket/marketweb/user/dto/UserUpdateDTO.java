package com.hagglemarket.marketweb.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter, setter 자동 생성
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 자동 생성 '레고 세트에 필요한 모든 부품을 한 번에 다 담아서 조립하는 느낌'
@NoArgsConstructor //파라미터가 없는 기본 생성자 자동 생성 '나중에 값을 천천히 채워서 만들고 싶다'
public class UserUpdateDTO {
    private String email;
    private String nickName;
    private String phoneNumber;
    private String address;
    private String imageURL;
}
