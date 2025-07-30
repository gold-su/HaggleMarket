package com.hagglemarket.marketweb.bid.repository;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.bid.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {

    //해당 경매글에 대한 입찰 내역을 입찰가 높은 순으로 조회 / 사용 용도 : 입찰 히스토리 보여주기, 최고가 입찰자 찾기 등
    List<BidHistory> findByAuctionPostOrderByBidAmountDesc(AuctionPost post); //특정 경매글의 입찰 내역을 입찰가 기준 내림차순으로 가져옴
}
