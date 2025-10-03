package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name = "shop_review")
public class ShopReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "shop_user_no", nullable = false)
    private Integer shopUserNo;

    @Column(name = "author_user_no", nullable = false)
    private Integer authorUserNo;

    // MySQL TINYINT ↔ Java Byte 매핑
    @Min(1) @Max(5)
    @Column(name = "rating", nullable = false)   // DB가 TINYINT
    private Byte rating;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}