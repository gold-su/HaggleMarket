package com.hagglemarket.marketweb.postlike.service;

import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.postlike.dto.LikeItemDto;
import com.hagglemarket.marketweb.postlike.entity.PostLike;
import com.hagglemarket.marketweb.postlike.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostRepository postRepository;
    private final AuctionPostRepository auctionPostRepository;
    private final PostLikeRepository postLikeRepository;

    /* ==================== 일반 게시물 ==================== */
    @Transactional
    public void like(int userNo, int postId) {
        if (postLikeRepository.existsByUserNoAndPostId(userNo, postId)) return;

        try {
            postLikeRepository.save(PostLike.builder()
                    .userNo(userNo)
                    .postId(postId)
                    .build());
            postRepository.incrementLikeCount(postId);
        } catch (DataIntegrityViolationException ignored) {}
    }

    @Transactional
    public void unLike(int userNo, int postId) {
        postLikeRepository.findByUserNoAndPostId(userNo, postId)
                .ifPresent(like -> {
                    postLikeRepository.delete(like);
                    postRepository.decrementLikeCount(postId);
                });
    }

    @Transactional(readOnly = true)
    public boolean isLiked(int userNo, int postId) {
        return postLikeRepository.existsByUserNoAndPostId(userNo, postId);
    }

    /* ==================== 경매 게시물 ==================== */
    @Transactional
    public void likeAuction(int userNo, int auctionId) {
        if (postLikeRepository.existsByUserNoAndAuctionId(userNo, auctionId)) return;

        try {
            postLikeRepository.save(PostLike.builder()
                    .userNo(userNo)
                    .auctionId(auctionId)
                    .build());
            auctionPostRepository.incrementLikeCount(auctionId);
        } catch (DataIntegrityViolationException ignored) {}
    }

    @Transactional
    public void unLikeAuction(int userNo, int auctionId) {
        postLikeRepository.findByUserNoAndAuctionId(userNo, auctionId)
                .ifPresent(like -> {
                    postLikeRepository.delete(like);
                    auctionPostRepository.decrementLikeCount(auctionId);

                });
    }

    @Transactional(readOnly = true)
    public boolean isAuctionLiked(int userNo, int auctionId) {
        return postLikeRepository.existsByUserNoAndAuctionId(userNo, auctionId);
    }

    /* ==================== 공통 (사이드바 등) ==================== */
    @Transactional(readOnly = true)
    public List<LikeItemDto> getMyLikes(int userNo) {
        return postLikeRepository.findMyLikes(userNo);
    }

    @Transactional(readOnly = true)
    public List<LikeItemDto> getMyAuctionLikes(int userNo) {
        List<LikeItemDto> list = postLikeRepository.findMyAuctionLikes(userNo);

        // 경매 이미지 URL 덧붙이기
        list.forEach(like -> {
            String thumb = like.getThumbnail();
            if (thumb != null && !thumb.isBlank() && !thumb.startsWith("/api/auction/images/")) {
                like.setThumbnail("/api/auction/images/" + thumb);
            }
        });

        return list;
    }


    @Transactional(readOnly = true)
    public List<LikeItemDto> getMyLikes(int userNo, int limit) {
        List<LikeItemDto> likes = postLikeRepository.findMyLikes(userNo);
        return likes.stream().limit(limit).toList();
    }
}
