package com.hagglemarket.marketweb.post.repository;

import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
}
