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
    public ResponseEntity<Void> like(@PathVariable int postId, @AuthenticationPrincipal CustomUserDetails user){
        postLikeService.like(user.getUserNo(), postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlike(@PathVariable int postId, @AuthenticationPrincipal CustomUserDetails user){
        postLikeService.unLike(user.getUserNo(), postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/like/me")
    @PreAuthorize("isAuthenticated()")
    public Map<String,Boolean> myLike(@PathVariable int postId,@AuthenticationPrincipal CustomUserDetails user){
        return Map.of("liked",postLikeService.isLiked(user.getUserNo(), postId));
    }

    @GetMapping("/likes/sidebar")
    public List<LikeItemDto> getSidebarLikes(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "20") int limit
    ) {
        if (user == null) {
            return List.of(); // 로그인 안 했으면 빈 목록
        }
        return postLikeService.getMyLikes(user.getUserNo(), limit);
    }

}
