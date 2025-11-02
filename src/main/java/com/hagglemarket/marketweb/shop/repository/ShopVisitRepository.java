package com.hagglemarket.marketweb.shop.repository;

import com.hagglemarket.marketweb.shop.entity.ShopVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopVisitRepository extends JpaRepository<ShopVisit, Long> {
}