package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductVisitService {

    private final PostRepository postRepo;

    @Transactional
    public void increaseHit(int postId) {
        postRepo.incrementHit(postId);
        System.out.println("✅ 조회수 +1 완료 (postId=" + postId + ")");
    }
}