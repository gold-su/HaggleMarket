package com.hagglemarket.marketweb.shop.repository;

import com.hagglemarket.marketweb.shop.entity.ShopVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopVisitRepository extends JpaRepository<ShopVisit, Long> {
    @Query("""
        SELECT COUNT(v) > 0 FROM ShopVisit v
        WHERE v.shopUserNo = :shopNo
          AND v.visitorUserNo = :visitorNo
          AND v.visitedDay = CURRENT_DATE
    """)
    boolean existsVisitToday(int shopNo, int visitorNo);
}
