package com.hagglemarket.marketweb.enddeal.repository;

import com.hagglemarket.marketweb.enddeal.entity.EndDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndDealRepository extends JpaRepository<EndDeal, Integer> {
}
