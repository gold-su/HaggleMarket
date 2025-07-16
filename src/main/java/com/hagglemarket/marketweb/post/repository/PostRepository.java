package com.hagglemarket.marketweb.post.repository;

import com.hagglemarket.marketweb.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
    
}