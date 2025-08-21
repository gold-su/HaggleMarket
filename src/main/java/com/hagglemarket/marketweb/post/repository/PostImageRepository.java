package com.hagglemarket.marketweb.post.repository;

import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPost(Post post);

    @Query("SELECT pi.imageUrl FROM PostImage pi WHERE pi.post.postId = :postId ORDER BY pi.sortOrder ASC")
    List<String> findImageUrlsByPostId(@Param("postId") int postId);
}