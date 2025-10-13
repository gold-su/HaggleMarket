package com.hagglemarket.marketweb.postlike.repository;

import com.hagglemarket.marketweb.postlike.dto.LikeItemDto;
import com.hagglemarket.marketweb.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    /* ===== 일반 게시물 ===== */
    boolean existsByUserNoAndPostId(int userNo, int postId);
    Optional<PostLike> findByUserNoAndPostId(int userNo, int postId);
    void deleteByUserNoAndPostId(int userNo, int postId);

    @Query("""
select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
    p.postId,
    p.title,
    pi.imageUrl,
    p.cost,
    false
)
from PostLike pl
join Post p on p.postId = pl.postId
left join PostImage pi on pi.post = p
  and pi.imageNo = (
      select min(pi3.imageNo) from PostImage pi3 where pi3.post = p
  )
where pl.userNo = :userNo
order by pl.createdAt desc
""")
    List<LikeItemDto> findMyLikes(int userNo);
    /* ===== 경매 게시물 ===== */
    boolean existsByUserNoAndAuctionId(int userNo, int auctionId);
    Optional<PostLike> findByUserNoAndAuctionId(int userNo, int auctionId);
    void deleteByUserNoAndAuctionId(int userNo, int auctionId);
    long countByPostId(int postId);

    @Query("""
select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
    a.auctionId,
    a.title,
    case when ai.imageId is null
         then null
         else concat('/api/auction/images/', concat('', ai.imageId))
    end,
    a.startCost,
    true
)
from PostLike pl
join AuctionPost a on a.auctionId = pl.auctionId
left join AuctionImage ai on ai.auctionPost = a
  and ai.imageId = (
      select min(ai2.imageId) from AuctionImage ai2 where ai2.auctionPost = a
  )
where pl.userNo = :userNo
order by pl.createdAt desc
""")
    List<LikeItemDto> findMyAuctionLikes(int userNo);
}
