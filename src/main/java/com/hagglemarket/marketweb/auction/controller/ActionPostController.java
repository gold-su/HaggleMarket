package com.hagglemarket.marketweb.auction.controller;

import com.hagglemarket.marketweb.auction.dto.AuctionPostRequest;
import com.hagglemarket.marketweb.auction.dto.AuctionPostResponse;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.service.AuctionPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //REST API 컨트롤러
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class ActionPostController {

    private final AuctionPostService auctionPostService;

    //경매 상품 등록 API
    @PostMapping("/create")
    public ResponseEntity<AuctionPostResponse> createAuctionPost(
            @RequestBody @Valid AuctionPostRequest request,
            @RequestParam("userNo") Integer userNo //URL의 쿼리스 스트링에서 값을 꺼내는 방식 : RequestParam
    ) {
        //서비스 호출
        AuctionPostResponse response = auctionPostService.createAuctionPost(request, userNo);

        //200 OK 응답
        return ResponseEntity.ok(response);
    }
}
