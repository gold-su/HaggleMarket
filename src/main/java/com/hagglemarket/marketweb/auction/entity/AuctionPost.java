package com.hagglemarket.marketweb.auction.entity;

import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction_posts")
@Data
public class AuctionPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="auction_id")
    private int auctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User seller;

    @Column(name = "category_id")
    private Integer category;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "start_cost", nullable = false)
    private int startCost;

    @Column(name = "current_cost", nullable = false)
    private int currentCost;

    @Column(name = "buyout_cost")
    private Integer buyoutCost;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "hit", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int hit = 0;

    @Column(name = "bid_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int bidCount = 0;

    /* ✅ 찜(좋아요) 개수 필드 추가 */
    @Column(name = "like_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status = AuctionStatus.READY;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_user_no")
    private User winner;

    @OneToMany(mappedBy = "auctionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImage> images = new ArrayList<>();
}
