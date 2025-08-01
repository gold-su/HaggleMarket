package com.hagglemarket.marketweb.auction.entity;

import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity //엔티티로 명시
@Table(name = "auction_posts") //DB 테이블 이름 저장 / 필수는 아니지만 정확성을 위해
@Data //getter, setter 자동 생성 / 하지만 실무에서는 무한 루프 발생 위험이 있어서 따로 쓰기도 함
public class AuctionPost {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT 전략
    @Column(name="auction_id") //실제 테이블과 매핑
    private int auctionId;

    @ManyToOne(fetch = FetchType.LAZY) //여러 상품 -> 한 사용자 (N:1 관계)
    @JoinColumn(name = "user_no", nullable = false) //FK, not null
    private User seller;

    @Column(name = "category_id")
    private Integer category;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "start_cost",nullable = false)
    private int startCost;

    @Column(name = "current_cost", nullable = false )
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

    @Enumerated(EnumType.STRING) //Enum을 문자열로 저장
    @Column(nullable = false)
    private AuctionStatus status = AuctionStatus.READY;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_user_no")
    private User winner;

    @OneToMany(mappedBy = "auctionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImage> images = new ArrayList<>();

}
