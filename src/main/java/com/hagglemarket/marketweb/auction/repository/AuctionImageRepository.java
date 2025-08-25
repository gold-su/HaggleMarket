package com.hagglemarket.marketweb.auction.repository;

import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Integer> {

    //특정 경매 상품에 속한 이미지 목록을 조회하는 메서드
    List<AuctionImage> findByAuctionPost_AuctionId(int auctionId);

    //특정 경매 상품에 연결된 이미지 전체 삭제용
    void deleteByAuctionPost(AuctionPost auctionPost);

    //특정 이미지 하나만 삭제 (예 : 프론트에서 이미지 X 버튼 눌렀을 때)
    void deleteById(int imageId);

    // sort_order 오름차순(동점이면 image_id 오름차순)으로 첫 이미지 1장
    Optional<AuctionImage> findFirstByAuctionPost_AuctionIdOrderBySortOrderAscImageIdAsc(int auctionId);
}
