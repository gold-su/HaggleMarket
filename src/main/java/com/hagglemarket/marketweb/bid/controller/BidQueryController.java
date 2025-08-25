package com.hagglemarket.marketweb.bid.controller;

import com.hagglemarket.marketweb.bid.dto.BidHistoryItemDTO;
import com.hagglemarket.marketweb.bid.service.BidQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction")
public class BidQueryController {

    //조회 로직을 위임받을 서비스
    private final BidQueryService Service;

    //입찰자 목록
    @GetMapping("/{auctionId}/bids")
    public Page<BidHistoryItemDTO> list(
            @PathVariable int auctionId, //경로 변수로 경매글 ID 수신
            @PageableDefault(size=20, sort = "bidTime", direction = Sort.Direction.DESC) Pageable pageable
            //기본 페이지 크기 20, bidTime 기준 내림차순 정렬 기본값
    ){
        //서비스에 위임하여 Page<BidHistoryItemDTO> 반환
        return Service.getBidHistory(auctionId, pageable);
    }

    //입찰 수 (배지/요약용)
    @GetMapping("/{auctionId}/bids/count")
    public long count(@PathVariable int auctionId){ //배지/요약 등에 사용할 총 입찰 수 조회
        return Service.count(auctionId); //long 으로 단순 수치 반환
    }
}
