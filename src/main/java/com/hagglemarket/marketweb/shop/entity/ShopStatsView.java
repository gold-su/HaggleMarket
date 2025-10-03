package com.hagglemarket.marketweb.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Getter
@Entity
@Immutable
@Table(name = "v_shop_stats")
public class ShopStatsView {
    @Id
    @Column(name = "user_no")
    private Integer userNo;

    @Column(name = "total_posts")           private Long totalPosts;
    @Column(name = "total_auctions")        private Long totalAuctions;
    @Column(name = "active_posts")          private Long activePosts;
    @Column(name = "active_auctions")       private Long activeAuctions;
    @Column(name = "sold_posts")            private Long soldPosts;
    @Column(name = "sold_auctions")         private Long soldAuctions;
    @Column(name = "total_likes_received")  private Long totalLikesReceived;
    @Column(name = "liked_by_me_total")     private Long likedByMeTotal;
    @Column(name = "followers")             private Long followers;
    @Column(name = "following")             private Long following;

    // DECIMAL ↔ BigDecimal
    // (정확히 맞추려면 뷰의 precision/scale을 보고 아래처럼 지정해도 됨)
    // @Column(name = "rating_avg", precision = 3, scale = 2)
    @Column(name = "rating_avg")
    private BigDecimal ratingAvg;

    @Column(name = "review_count")          private Long reviewCount;
}