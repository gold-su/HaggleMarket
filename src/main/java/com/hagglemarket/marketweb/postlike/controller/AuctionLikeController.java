package com.hagglemarket.marketweb.postlike.controller;

import com.hagglemarket.marketweb.postlike.service.PostLikeService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{auctionId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> likeAuction(@PathVariable int auctionId,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        postLikeService.likeAuction(user.getUserNo(), auctionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{auctionId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeAuction(@PathVariable int auctionId,
                                              @AuthenticationPrincipal CustomUserDetails user) {
        postLikeService.unLikeAuction(user.getUserNo(), auctionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{auctionId}/like/me")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Boolean> myAuctionLike(@PathVariable int auctionId,
                                              @AuthenticationPrincipal CustomUserDetails user) {
        return Map.of("liked", postLikeService.isAuctionLiked(user.getUserNo(), auctionId));
    }
}
