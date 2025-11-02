package com.hagglemarket.marketweb.auction.repository;

import com.hagglemarket.marketweb.auction.dto.AuctionDetailDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionListDTO;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AuctionPostRepository extends JpaRepository<AuctionPost, Integer> {

    @Query("SELECT new com.hagglemarket.marketweb.auction.dto.AuctionListDTO(" +
            "p.auctionId, p.title, i.imageName, p.currentCost, p.endTime, p.hit, p.bidCount)" +
            "FROM AuctionPost p " +
            "LEFT JOIN p.images i with i.sortOrder = 1 " +
            "ORDER BY p.createdAt DESC")
    List<AuctionListDTO> findAllWithThumbnail();

    List<AuctionPost> findByEndTimeBeforeAndStatus(LocalDateTime now, AuctionStatus status);

    List<AuctionPost> findByStartTimeBeforeAndStatus(LocalDateTime now, AuctionStatus status);

    List<AuctionPost> findByAuctionIdIn(Collection<Integer> ids);

    /* ===== 찜(좋아요) 관련 ===== */
    @Modifying
    @Query("UPDATE AuctionPost a SET a.likeCount = a.likeCount + 1 WHERE a.auctionId = :auctionId")
    void incrementLikeCount(@Param("auctionId") int auctionId);

    @Modifying
    @Query("UPDATE AuctionPost a SET a.likeCount = a.likeCount - 1 WHERE a.auctionId = :auctionId")
    void decrementLikeCount(@Param("auctionId") int auctionId);

    @Query("SELECT p FROM AuctionPost p " +
            "LEFT JOIN FETCH p.images i " +
            "WHERE p.status <> com.hagglemarket.marketweb.auction.entity.AuctionStatus.ENDED " +
            "ORDER BY p.endTime ASC")
    List<AuctionPost> findOngoingSortedByEndTime();

    @Query("SELECT p FROM AuctionPost p " +
            "LEFT JOIN FETCH p.images i " +
            "WHERE p.status = com.hagglemarket.marketweb.auction.entity.AuctionStatus.ENDED " +
            "ORDER BY p.endTime DESC")
    List<AuctionPost> findEndedSorted();

}
