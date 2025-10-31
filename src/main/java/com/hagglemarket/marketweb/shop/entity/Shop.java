package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shop")
public class Shop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="shop_id")
    private int shopId;

    @Column(name = "user_no", nullable = false, unique = true)
    private Integer userNo;

    @Column(name = "nickname", nullable = false, length = 40)
    private String nickname;

    @Column(name = "intro", length = 300)
    private String intro;

    @Column(name = "profile_url", length = 255)
    private String profileUrl;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "visit_count", nullable = false)
    private Integer visitCount;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        var now = LocalDateTime.now();
        if (openedAt == null) openedAt = now;
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (visitCount == null) visitCount = 0;
    }

    public static Shop ofUser(int userNo) {
        return Shop.builder()
                .userNo(userNo)
                .nickname("상점" + userNo + "호")
                .intro("")
                .verified(false)
                .build();
    }
}
