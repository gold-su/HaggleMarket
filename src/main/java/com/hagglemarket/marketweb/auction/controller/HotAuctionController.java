package com.hagglemarket.marketweb.auction.controller;

import com.hagglemarket.marketweb.auction.dto.HotAuctionItemDTO;
import com.hagglemarket.marketweb.auction.service.HotAuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction")
public class HotAuctionController {

    //인기 경매 조회 비즈니스 로직을 담당하느 서비스 주입
    private final HotAuctionService Service;

    @GetMapping("/hot")
    public Page<HotAuctionItemDTO> hot(
            @PageableDefault(size=12) Pageable pageable   //페이징 파라미터 자동 바인딩. 기본 페이지 크기를 12로 설정
    ){
        //서비스 계층에 위임하여 Page<HotAuctionItemDTO>를 그대로 반환
        return Service.getHot(pageable);
    }
}
