package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.shop.dto.ShopProfileDto;
import com.hagglemarket.marketweb.shop.dto.ShopProfileUpdateRequest;
import com.hagglemarket.marketweb.shop.dto.ShopStatsDto;
import com.hagglemarket.marketweb.shop.entity.Shop;
import com.hagglemarket.marketweb.shop.entity.ShopStatsView;
import com.hagglemarket.marketweb.shop.repository.ShopRepository;
import com.hagglemarket.marketweb.shop.repository.ShopStatsViewRepository;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;  // ✅ 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepo;
    private final ShopStatsViewRepository statsRepo;
    private final UserRepository userRepo;  // ✅ 추가
    private final PostRepository postRepository;          // ✅ 추가
    private final AuctionPostRepository auctionRepository; // ✅ 추가

    @Transactional
    public ShopProfileDto getOrCreateProfile(int userNo) {
        Shop shop = shopRepo.findByUserNo(userNo)
                .orElseGet(() -> shopRepo.save(Shop.ofUser(userNo)));
        return toDto(shop);
    }

    @Transactional(readOnly = true)
    public ShopProfileDto getProfile(int userNo) {
        Shop shop = shopRepo.findByUserNo(userNo)
                .orElseThrow(() -> new IllegalArgumentException("상점이 존재하지 않습니다: " + userNo));
        return toDto(shop);
    }

    @Transactional
    public void updateProfile(int userNo, ShopProfileUpdateRequest req) {
        Shop shop = shopRepo.findByUserNo(userNo)
                .orElseGet(() -> shopRepo.save(Shop.ofUser(userNo)));

        if (req.getNickname() != null && !req.getNickname().isBlank()) {
            shop.setNickname(req.getNickname().trim());
        }
        if (req.getIntro() != null) {
            shop.setIntro(req.getIntro());
        }
        if (req.getProfileUrl() != null) {
            shop.setProfileUrl(req.getProfileUrl());
        }
        // 변경감지로 자동 flush
    }

    @Transactional(readOnly = true)
    public ShopStatsDto getStats(int userNo) {
        ShopStatsView v = statsRepo.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("통계가 존재하지 않습니다: " + userNo));

        var openDate = shopRepo.findByUserNo(userNo)
                .map(Shop::getOpenedAt)
                .orElse(null);

        return ShopStatsDto.builder()
                .totalProducts(nz(v.getTotalPosts()))
                .activeProducts(nz(v.getActivePosts()))
                .soldProducts(nz(v.getSoldPosts()))
                .totalAuctions(nz(v.getTotalAuctions()))
                .activeAuctions(nz(v.getActiveAuctions()))
                .soldAuctions(nz(v.getSoldAuctions()))
                .totalLikes(nz(v.getTotalLikesReceived()))
                .followers(nz(v.getFollowers()))
                .following(nz(v.getFollowing()))
                .ratingAvg(scale2(nzb(v.getRatingAvg())))
                .reviewCount(nz(v.getReviewCount()))
                .storeOpenedAt(openDate)
                .build();
    }

    @Transactional
    public void updateIntro(int userNo, String intro) {
        Shop shop = shopRepo.findByUserNo(userNo)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        shop.setIntro(intro);
    }

    private long nz(Long v) { return v == null ? 0L : v; }
    private BigDecimal nzb(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private BigDecimal scale2(BigDecimal v) { return v.setScale(2, RoundingMode.HALF_UP); }

    private ShopProfileDto toDto(Shop s) {
        // ✅ User 엔티티 조회
        User user = userRepo.findById(s.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return ShopProfileDto.builder()
                .userNo(s.getUserNo())
                .nickname(user.getNickName()) // ✅ 항상 최신 닉네임 사용
                .intro(s.getIntro())
                // ✅ 핵심 수정 부분: Shop에 없으면 User.imageURL 사용
                .profileUrl(
                        s.getProfileUrl() != null ? s.getProfileUrl() : user.getImageURL()
                )
                .verified(s.isVerified())
                .storeOpenedAt(s.getOpenedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ShopProfileDto getShopProfile(int userNo) {
        Shop shop = shopRepo.findByUserNo(userNo)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        return toDto(shop);
    }
}
