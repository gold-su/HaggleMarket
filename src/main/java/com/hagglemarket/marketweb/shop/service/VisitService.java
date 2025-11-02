package com.hagglemarket.marketweb.shop.service;

import com.hagglemarket.marketweb.post.repository.PostRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final ShopVisitTxService shopVisitTxService;
    private final PostRepository postRepo;

    public void recordVisit(String type, int targetId, Integer visitorUserNo, HttpSession session) {
        if (visitorUserNo == null) return;

        String visitKey = type + "_visited_" + targetId;
        if (session.getAttribute(visitKey) != null) return;

        try {
            switch (type.toUpperCase()) {
                case "SHOP" -> shopVisitTxService.saveVisit(targetId, visitorUserNo);
                case "PRODUCT" -> recordProductVisit(targetId);
            }
        } catch (Exception ignored) {
            System.out.println("⚠️ VisitService 예외 무시 (rollback 방지)");
        }

        session.setAttribute(visitKey, true);
    }

    private void recordProductVisit(int postId) {
        try {
            postRepo.incrementHit(postId);
        } catch (Exception e) {
            System.out.println("⚠️ 조회수 증가 실패 (무시됨)");
        }
    }
}
