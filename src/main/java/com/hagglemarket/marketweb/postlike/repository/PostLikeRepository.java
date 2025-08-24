package com.hagglemarket.marketweb.postlike.repository;

import com.hagglemarket.marketweb.postlike.dto.LikeItemDto;
import com.hagglemarket.marketweb.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // ==== POST 전용 ====
    boolean existsByUserNoAndPostId(int userNo, int postId);
    Optional<PostLike> findByUserNoAndPostId(int userNo, int postId);
    long countByPostId(int postId);
    void deleteByUserNoAndPostId(int userNo, int postId);

    // ==== AUCTION 전용 ====
    boolean existsByUserNoAndAuctionId(int userNo, int auctionId);
    Optional<PostLike> findByUserNoAndAuctionId(int userNo, int auctionId);
    long countByAuctionId(int auctionId);
    void deleteByUserNoAndAuctionId(int userNo, int auctionId);

    // 내가 찜한 일반상품
    @Query("""
    select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
        'POST',
        p.postId,
        p.title,
        (
           select pi2.imageUrl
           from PostImage pi2
           where pi2.post = p
             and pi2.imageNo = (
                 select min(pi3.imageNo)
                 from PostImage pi3
                 where pi3.post = p
             )
        ),
        pl.createdAt
    )
    from PostLike pl
    join Post p on p.postId = pl.postId
    where pl.userNo = :userNo and pl.postId is not null
    order by pl.createdAt desc
    """)
    List<LikeItemDto> findMyPostLikes(int userNo);

    // 내가 찜한 경매상품 (엔티티명: AuctionPost / 이미지: AuctionImage)
    @Query("""
select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
  'AUCTION',
  a.auctionId,
  a.title,
  (select ai2.imageUrl
     from AuctionImage ai2
    where ai2.auctionPost = a
      and ai2.id = (select min(ai3.id) from AuctionImage ai3 where ai3.auctionPost = a)),
  pl.createdAt
)
from PostLike pl
join AuctionPost a on a.auctionId = pl.auctionId
where pl.userNo = :userNo and pl.auctionId is not null
order by pl.createdAt desc
""")
    List<LikeItemDto> findMyAuctionLikes(int userNo);
}
