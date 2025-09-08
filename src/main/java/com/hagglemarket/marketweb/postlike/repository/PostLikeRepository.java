package com.hagglemarket.marketweb.postlike.repository;

import com.hagglemarket.marketweb.postlike.dto.LikeItemDto;
import com.hagglemarket.marketweb.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    boolean existsByUserNoAndPostId(int userNo, int postId);
    Optional<PostLike> findByUserNoAndPostId(int userNo, int postId);

    long countByPostId(int postId);
    void deleteByUserNoAndPostId(int userNo, int postId);

    @Query("""
    select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
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
        )
    )
    from PostLike pl
    join Post p on p.postId = pl.postId
    where pl.userNo = :userNo
    order by pl.createdAt desc
""")
    List<LikeItemDto> findMyLikes(int userNo);

    // PostLikeRepository.java
    @Query("""
select new com.hagglemarket.marketweb.postlike.dto.LikeItemDto(
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
    )
)
from PostLike pl
join Post p on p.postId = pl.postId
where pl.userNo = :userNo
order by pl.createdAt desc
""")
    List<LikeItemDto> findMyLikes(int userNo, org.springframework.data.domain.Pageable pageable);

}
