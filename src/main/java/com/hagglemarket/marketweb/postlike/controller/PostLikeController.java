package com.hagglemarket.marketweb.postlike.controller;

import com.hagglemarket.marketweb.postlike.dto.LikeItemDto;
import com.hagglemarket.marketweb.postlike.service.PostLikeService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(@PathVariable int postId, @AuthenticationPrincipal CustomUserDetails user) {
        postLikeService.like(user.getUserNo(), postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlike(@PathVariable int postId, @AuthenticationPrincipal CustomUserDetails user) {
        postLikeService.unLike(user.getUserNo(), postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/like/me")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Boolean> myLike(@PathVariable int postId, @AuthenticationPrincipal CustomUserDetails user) {
        return Map.of("liked", postLikeService.isLiked(user.getUserNo(), postId));
    }

    @GetMapping("/likes/sidebar")
    public List<LikeItemDto> getSidebarLikes(@AuthenticationPrincipal CustomUserDetails user,
                                             @RequestParam(defaultValue = "20") int limit) {
        if (user == null) return List.of();

        // 일반 찜 + 경매 찜 둘 다 합치기
        List<LikeItemDto> postLikes = postLikeService.getMyLikes(user.getUserNo(), limit);
        List<LikeItemDto> auctionLikes = postLikeService.getMyAuctionLikes(user.getUserNo());

        // 병합 후 최근순 정렬 (createdAt 기준은 없으니 단순히 합치기)
        postLikes.addAll(auctionLikes);
        return postLikes;
    }
}
