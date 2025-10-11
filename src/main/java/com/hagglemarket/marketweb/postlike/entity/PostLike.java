package com.hagglemarket.marketweb.postlike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_post", columnNames = {"user_no", "post_id"}),
                @UniqueConstraint(name = "uq_user_auction", columnNames = {"user_no", "auction_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_no", nullable = false)
    private int userNo;

    @Column(name = "post_id")
    private Integer postId;   // 일반 게시물 찜용

    @Column(name = "auction_id")
    private Integer auctionId; // ✅ 경매 찜용 (이게 없으면 builder 오류 발생)

    @Column(name = "created_at", nullable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt = LocalDateTime.now();
}
