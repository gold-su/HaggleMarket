package com.hagglemarket.marketweb.post.repository;

import com.hagglemarket.marketweb.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.postId = :postId")
    Optional<Post> findByIdWithImages(@Param("postId") int postId);

    //카테고리아이디로 내림차순정렬
    List<Post> findByCategoryIdOrderByCreatedAtDesc(Integer categoryId);

    @Modifying
    @Query(value="UPDATE posts SET like_count = like_count + 1 WHERE post_id=:postId", nativeQuery=true)
    int incrementLikeCount(@Param("postId") int postId);
    @Modifying
    @Query(value="UPDATE posts SET like_count = GREATEST(like_count - 1, 0) WHERE post_id=:postId", nativeQuery=true)
    int decrementLikeCount(@Param("postId") int postId);
}