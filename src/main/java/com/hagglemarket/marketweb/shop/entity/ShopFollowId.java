package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode        // 복합키는 반드시 equals/hashCode 구현
@Embeddable
public class ShopFollowId implements Serializable {

    @Column(name = "follower_user_no", nullable = false)
    private Integer followUserId;

    @Column(name = "shop_user_no", nullable = false)
    private Integer shopUserNo;
}