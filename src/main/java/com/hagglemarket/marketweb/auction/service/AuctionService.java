package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionPostRepository auctionPostRepository;

    public Integer getSellerUserNoByAuctionId(Integer auctionId) {
        return auctionPostRepository.findById(auctionId)
                .map(a -> a.getSeller().getUserNo())
                .orElseThrow(()->new IllegalArgumentException("해당 경매를 찾을 수 없습니다."));
    }
}
