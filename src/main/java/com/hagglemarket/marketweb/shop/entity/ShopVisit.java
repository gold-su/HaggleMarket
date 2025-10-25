package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "shop_visit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_user_no", nullable = false)
    private int shopUserNo;

    @Column(name = "visitor_user_no")
    private Integer visitorUserNo;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Column(name = "visited_day", insertable = false, updatable = false)
    private LocalDate visitedDay;

    @PrePersist
    public void prePersist() {
        if (visitedAt == null) visitedAt = LocalDateTime.now();
    }

    public ShopVisit(int shopUserNo, Integer visitorUserNo) {
        this.shopUserNo = shopUserNo;
        this.visitorUserNo = visitorUserNo;
        this.visitedAt = LocalDateTime.now();
    }
}
