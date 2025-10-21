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

    private long nz(Long v) { return v == null ? 0L : v; }
    private BigDecimal nzb(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private BigDecimal scale2(BigDecimal v) { return v.setScale(2, RoundingMode.HALF_UP); }

    private ShopProfileDto toDto(Shop s) {
        // ✅ User 테이블에서 nickname을 가져오도록 수정
        String nickname = userRepo.findById(s.getUserNo())
                .map(User::getNickName)
                .orElse("사용자");

        return ShopProfileDto.builder()
                .userNo(s.getUserNo())
                .nickname(nickname)  // ✅ user nickname 사용
                .intro(s.getIntro())
                .profileUrl(s.getProfileUrl())
                .verified(s.isVerified())
                .storeOpenedAt(s.getOpenedAt())
                .build();
    }
}
