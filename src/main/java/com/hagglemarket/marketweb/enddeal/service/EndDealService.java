package com.hagglemarket.marketweb.enddeal.service;

import com.hagglemarket.marketweb.enddeal.entity.EndDeal;
import com.hagglemarket.marketweb.enddeal.repository.EndDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EndDealService {

    private final EndDealRepository endDealRepository;

    public void recordEndDeal(String type, int itemId, int userNo, String title,
                              Integer finalPrice, String reason, String status) {

        EndDeal endDeal = EndDeal.builder()
                .type(type)
                .postId("used".equalsIgnoreCase(type) ? itemId : null)
                .auctionId("auction".equalsIgnoreCase(type) ? itemId : null)
                .userNo(userNo)
                .title(title)
                .finalPrice(finalPrice)
                .status(status)
                .reason(reason)
                .endTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        endDealRepository.save(endDeal);
    }
}
