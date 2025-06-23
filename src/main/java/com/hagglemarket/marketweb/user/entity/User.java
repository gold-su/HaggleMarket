package com.hagglemarket.marketweb.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userNo")
    private int userNo;

    @Column(name = "userId", unique = true, nullable = false, length = 20)
    private String userId;

    @Column(name = "userName", nullable = false, length = 20)
    private String userName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phoneNumber", nullable = false, length = 11, unique = true)
    private String phoneNumber;

    @Column(name = "nickName", nullable = false, length = 15)
    private String nickName;

    @Column(name = "address", nullable = false, length = 30)
    private String address;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "imageURL", columnDefinition = "TEXT")
    private String imageURL;

    @Column(name = "created", updatable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        DELETED,
        ADMIN
    }

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "roadRating")
    private BigDecimal roadRating;
}