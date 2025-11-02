package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.shop.entity.ShopVisit;
import com.hagglemarket.marketweb.shop.repository.ShopRepository;
import com.hagglemarket.marketweb.shop.repository.ShopVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShopVisitTxService {

    private final ShopVisitRepository visitRepo;
    private final ShopRepository shopRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DataIntegrityViolationException.class)
    public void saveVisit(int shopUserNo, int visitorUserNo) {
        if (shopUserNo == visitorUserNo) return;

        try {
            visitRepo.save(ShopVisit.builder()
                    .shopUserNo(shopUserNo)
                    .visitorUserNo(visitorUserNo)
                    .visitedAt(LocalDateTime.now())
                    .build());
            shopRepo.incrementVisitCount(shopUserNo);
        } catch (DataIntegrityViolationException e) {
            System.out.println("⚠️ 중복 방문 무시됨: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ 방문 저장 중 오류: " + e.getMessage());
        }
    }
}
