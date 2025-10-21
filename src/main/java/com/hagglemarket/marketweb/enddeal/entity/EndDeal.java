package com.hagglemarket.marketweb.enddeal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "end_deal")  // ✅ DB 테이블명과 일치시킴
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enddeal_id")
    private int enddealId;

    @Column(name = "type")
    private String type;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "auction_id")   // ✅ DB 컬럼명과 정확히 매칭
    private Integer auctionId;

    @Column(name = "user_no")
    private int userNo;

    @Column(name = "title")
    private String title;

    @Column(name = "final_price")
    private Integer finalPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
