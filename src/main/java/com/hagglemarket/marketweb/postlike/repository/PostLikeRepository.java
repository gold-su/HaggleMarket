package com.hagglemarket.marketweb.postlike.repository;

import com.hagglemarket.marketweb.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    boolean existsByUserNoAndPostId(int userNo, int postId);
    Optional<PostLike> findByUserNoAndPostId(int userNo, int postId);

    long countByPostId(int postId);
    void deleteByUserNoAndPostId(int userNo, int postId);
}
