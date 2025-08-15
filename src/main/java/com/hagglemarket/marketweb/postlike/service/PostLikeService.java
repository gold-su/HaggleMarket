package com.hagglemarket.marketweb.postlike.service;

import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.postlike.entity.PostLike;
import com.hagglemarket.marketweb.postlike.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void like(int userNo, int postId) {
        if(postLikeRepository.existsByUserNoAndPostId(userNo, postId))
            return;
        try {
            postLikeRepository.save(PostLike.builder().userNo(userNo).postId(postId).build());
            postRepository.incrementLikeCount(postId);
        }catch (DataIntegrityViolationException e){

        }
    }

    @Transactional
    public void unLike(int userNo, int postId) {
        postLikeRepository.findByUserNoAndPostId(userNo, postId).ifPresent(pl ->{
            postLikeRepository.delete(pl);
            postRepository.decrementLikeCount(postId);
        });
    }

    @Transactional(readOnly = true)
    public boolean isLiked(int userNo, int postId) {
        return postLikeRepository.existsByUserNoAndPostId(userNo, postId);
    }
}
