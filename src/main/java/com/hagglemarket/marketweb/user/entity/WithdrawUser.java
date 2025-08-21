package com.hagglemarket.marketweb.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder //빌더 패턴으로 객체 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 생성 (보안을 위한 protected)
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@Table(name = "withdraw_users")
public class WithdrawUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Integer no;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;
}
