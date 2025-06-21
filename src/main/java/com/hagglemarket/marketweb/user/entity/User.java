package com.hagglemarket.marketweb.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user") //테이블 매핑
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 생성 (보안을 위한 protected)
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@Builder //빌더 패턴으로 객체 생성
//유저 테이블 entity
public class User {
    @Id //기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키 값 자동 생성(생성타입 identity[++1 값 늘리는 형식]
    private int userNo;

    @Column(unique = true, nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 20)
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 11, unique = true)
    private String phoneNumber;

    @Column(nullable = false, length = 15)
    private String nickName;

    @Column(nullable = false, length = 30)
    private String address;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT") //이미지 URL을 text로 저장할 수 있게 설정
    private String imageURL;

    //업데이트 불가
    @Column(updatable = false)
    //자동으로 현재시간 넣기
    @CreationTimestamp
    //직접 NOW()넣거나, DB에 default 설정하고 insertable = false도 가능 [LocalDateTime]
    private LocalDateTime create;

    //미리 정해진 값들 중 하나를 선택하도록 강제하는 열거형 타입.
    @Enumerated(EnumType.STRING) //<- string 타입으로 설정, .ORDINAL로 하면 (0,1,2)
    private UserStatus status;
    //위 status의 값을 미리 설정
    public enum UserStatus {
        ACTIVE, //활성
        INACTIVE, //비활성
        DELETED, //삭제됨
        ADMIN //관리자
    }

    //평점처럼 소수점 계산이 필요한 값에 주로 사용 [BigDecimal]
    private BigDecimal rating;

    private BigDecimal roadRating;
}