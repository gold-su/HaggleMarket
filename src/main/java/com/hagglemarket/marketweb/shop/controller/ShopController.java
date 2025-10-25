package com.hagglemarket.marketweb.shop.controller;

import com.hagglemarket.marketweb.shop.dto.*;
import com.hagglemarket.marketweb.shop.service.ProductQueryService;
import com.hagglemarket.marketweb.shop.service.ShopService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import com.hagglemarket.marketweb.shop.service.ShopVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final ProductQueryService productQueryService;
    private final ShopVisitService shopVisitService;

    // 공개: 상점 프로필
    @GetMapping("/{userNo}")
    public ShopProfileDto getShop(
            @PathVariable int userNo,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        //방문 기록 추가
        shopVisitService.recordVisit(userNo, user != null ? user.getUserNo() : null);

        return shopService.getProfile(userNo);
    }

    // 공개: 상점 통계(집계뷰)
    @GetMapping("/{userNo}/stats")
    public ShopStatsDto stats(@PathVariable int userNo) {
        return shopService.getStats(userNo);
    }

    // 공개: 판매자 상품 목록(중고/경매 분기)
    @GetMapping("/{userNo}/products")
    public PageResponse<PostCardDto> products(@PathVariable int userNo,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "12") int size,
                                              @RequestParam(defaultValue = "latest") String sort,
                                              @RequestParam(defaultValue = "used") String type) {
        return productQueryService.findBySeller(userNo, page, size, sort, type);
    }

    // 본인: 내 상점 조회(없으면 생성)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ShopProfileDto me(@AuthenticationPrincipal CustomUserDetails me) {
        return shopService.getOrCreateProfile(me.getUserNo());
    }

    // 본인: 내 상점 수정
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public void updateMe(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestBody ShopProfileUpdateRequest req) {
        shopService.updateProfile(me.getUserNo(), req);
    }

    @PutMapping("/{userNo}/intro")
    public ResponseEntity<Void> updateIntro(
            @PathVariable int userNo,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null || user.getUserNo() != userNo) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String intro = body.get("intro");
        shopService.updateIntro(userNo, intro);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userNo}/detail")
    public ResponseEntity<ShopProfileDto> getShopProfile(@PathVariable int userNo) {
        ShopProfileDto profile = shopService.getShopProfile(userNo);
        return ResponseEntity.ok(profile);
    }
}