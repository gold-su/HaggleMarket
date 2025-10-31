package com.hagglemarket.marketweb.shop.repository;

import com.hagglemarket.marketweb.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
    Optional<Shop> findByUserNo(Integer userNo);
    boolean existsByNickname(String nickname);
}