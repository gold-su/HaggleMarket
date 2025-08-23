package com.hagglemarket.marketweb.bid.entity;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.query.Page;

import java.time.LocalDateTime;

@Entity //엔티티로 명시
@Table(name = "bids" )  //Table 이름 명시
@Getter @Setter  //가져오고 set할 수 있게 명시
@NoArgsConstructor //기본 생성자 생성
@AllArgsConstructor //전체 필드를 받는 생성자 생성
public class BidHistory {

    @Id //기본키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment
    @Column(name = "bid_id", nullable = false)
    private int bidId;

    //어떤 경매글에 입찰했는지 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="auction_id", nullable=false)
    private AuctionPost auctionPost;

    //누가 입찰혔는지 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_user_no", nullable=false)
    private User bidder;

    //입찰 금액
    @Column(name = "bid_amount",nullable = false)
    private Integer bidAmount;

    //입찰한 시간
    @Column(name = "bid_time",nullable = false)
    private LocalDateTime bidTime = LocalDateTime.now();
}
