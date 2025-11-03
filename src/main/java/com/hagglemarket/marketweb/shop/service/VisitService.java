package com.hagglemarket.marketweb.shop.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final ShopVisitTxService shopVisitTxService;
    private final ProductVisitService productVisitService; // ✅ 새 서비스 주입

    public void recordVisit(String type, int targetId, Integer visitorUserNo, HttpSession session) {
        if (visitorUserNo == null) return;

        String visitKey = type + "_visited_" + targetId;
        if (session.getAttribute(visitKey) != null) return;

        try {
            switch (type.toUpperCase()) {
                case "SHOP" -> shopVisitTxService.saveVisit(targetId, visitorUserNo);
                case "PRODUCT" -> productVisitService.increaseHit(targetId); // ✅ 분리된 서비스 호출
            }
        } catch (Exception e) {
            System.out.println("⚠️ VisitService 예외 무시: " + e.getMessage());
            e.printStackTrace();
        }

        session.setAttribute(visitKey, true);
    }
}