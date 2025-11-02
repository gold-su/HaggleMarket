package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "shop_visit",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"shop_user_no", "visitor_user_no", "visited_day"})
        },
        indexes = {
                @Index(name = "idx_shop_day", columnList = "shop_user_no, visited_day")
        }
)
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
}
