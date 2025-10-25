package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.shop.entity.ShopVisit;
import com.hagglemarket.marketweb.shop.repository.ShopRepository;
import com.hagglemarket.marketweb.shop.repository.ShopVisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopVisitService {
    private final ShopVisitRepository visitRepo;
    private final ShopRepository shopRepo;

    @Transactional
    public void recordVisit(int shopUserNo, Integer visitorUserNo) {
        if (visitorUserNo != null && shopUserNo == visitorUserNo) return; // 자기 자신 제외

        boolean alreadyVisited = false;
        if (visitorUserNo != null) {
            alreadyVisited = visitRepo.existsVisitToday(shopUserNo, visitorUserNo);
        }

        if (!alreadyVisited) {
            visitRepo.save(new ShopVisit(shopUserNo, visitorUserNo));
            shopRepo.incrementVisitCount(shopUserNo);
        }
    }
}
