package com.hagglemarket.marketweb.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
//회원가입 시 사용자가 입력할 항목
public class UserJoinDTO {

    @NotBlank //값이 null,""," "등이면 검증 실패/string 타입에만 사용
    @Size(min = 5, max = 20) // 최소 5자 최대 20자
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "아이디는 영문자와 숫자 조합의 5~20자여야 합니다.")
    private String userId;

    @NotBlank
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
    private String password;

    @NotBlank
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣]{2,10}$", message = "이름은 한글 2~10자여야 합니다.")
    private String userName;

    @NotBlank
    @Size(min = 2, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,15}$", message = "닉네임은 특수문자를 제외한 2~15자여야 합니다.")
    private String nickName;

    @NotBlank
    @Size(min = 10, max = 11)
    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 '-' 없이 숫자 11자여야 합니다.")
    private String phoneNumber;

    @NotBlank
    @Email //값이 email 형식인지 검증
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank
    @Size(max = 30)
    private String address;

    private String imageURL;

}
