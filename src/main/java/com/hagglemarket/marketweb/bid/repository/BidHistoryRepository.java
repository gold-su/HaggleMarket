package com.hagglemarket.marketweb.bid.repository;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.bid.entity.AuctionBidCount;
import com.hagglemarket.marketweb.bid.entity.BidHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {

    //해당 경매글에 대한 입찰 내역을 입찰가 높은 순으로 조회 / 사용 용도 : 입찰 히스토리 보여주기, 최고가 입찰자 찾기 등
    List<BidHistory> findByAuctionPostOrderByBidAmountDesc(AuctionPost post); //특정 경매글의 입찰 내역을 입찰가 기준 내림차순으로 가져옴

    //auctionPost 가 파라미터로 들어온 AuctionPost 객체인 BidHistory 레코드들을 모두 가져온다.
    List<BidHistory> findByAuctionPost(AuctionPost post);

    //페이지네이션 조회
    @EntityGraph(attributePaths = {"bidder"})
    @Query("""
     select b
     from BidHistory b
     join b.auctionPost ap
     where ap.auctionId = :auctionId
     order by b.bidTime desc
    """)
    Page<BidHistory> findPageByAuctionId(@Param("auctionId") int auctionId, Pageable pageable);

    //가장 최근 입찰 1건
    Optional<BidHistory> findFirstByAuctionPost_AuctionIdOrderByBidTimeDesc(int auctionId);

    @Query(
            value = """
    select b.auctionPost.auctionId as auctionId, count(b) as bidCount
    from BidHistory b
    group by b.auctionPost.auctionId
    order by count(b) desc
    """,
            countQuery = """
    select count(distinct b.auctionPost.auctionId)
    from BidHistory b
    """
    )
    Page<AuctionBidCount> findHotAuctionIds(Pageable pageable);

}
