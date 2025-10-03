package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name = "shop_follow")
public class ShopFollow {

    @EmbeddedId
    private ShopFollowId id;   // 컬럼 매핑은 이제 Embeddable 안에서 끝

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onPrePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}