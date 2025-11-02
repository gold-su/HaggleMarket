package com.hagglemarket.marketweb.shop.repository;

import com.hagglemarket.marketweb.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
    Optional<Shop> findByUserNo(Integer userNo);
    boolean existsByNickname(String nickname);

    @Modifying
    @Query("UPDATE Shop s SET s.visitCount = s.visitCount + 1 WHERE s.userNo = :userNo")
    void incrementVisitCount(int userNo);
}