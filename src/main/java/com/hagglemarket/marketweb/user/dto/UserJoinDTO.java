package com.hagglemarket.marketweb.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
//회원가입 시 사용자가 입력할 항목
public class UserJoinDTO {

    @NotBlank //값이 null,""," "등이면 검증 실패/string 타입에만 사용
    @Size(min = 5, max = 20) // 최소 5자 최대 20자
    private String userId;

    @NotBlank
    @Size(min = 5, max = 20)
    private String password;

    @NotBlank
    @Size(min = 2, max = 15)
    private String nickName;

    @NotBlank
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @NotBlank
    @Email //값이 email 형식인지 검증
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String address;

    private String imageURL;

}
