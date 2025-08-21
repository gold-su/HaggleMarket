package com.hagglemarket.marketweb.bid.controller;

import com.hagglemarket.marketweb.bid.dto.BidRequestDTO;
import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import com.hagglemarket.marketweb.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //json 반환 컨트롤러
@RequiredArgsConstructor  //final로 선언된 필드 기반 생성자 자동 생성 (DI 주입용)
@RequestMapping("/api/auction") //공옽 URI 설정
public class BidController {

    private final BidService bidService;

    //입찰 요청
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<BidResponseDTO> placeBid(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.hagglemarket.marketweb.security.CustomUserDetails user,
            @PathVariable int auctionId,
            @RequestBody BidRequestDTO request
    ) {
        if(user == null){
            return ResponseEntity.status(401).build();
        }
        //바디에서 auctionId 받지 않고 path를 신뢰 (혼선을 막기 위해 통일)
        request.setUserNo(user.getUserNo());
        request.setAuctionId(auctionId);
        return ResponseEntity.ok(bidService.placeBid(request));
    }

    @PostMapping("/{auctionId}/buyout")
    public ResponseEntity<BidResponseDTO> buyout(
            @PathVariable int auctionId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.hagglemarket.marketweb.security.CustomUserDetails user
    ){
        if(user == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."
            );
        }
        return ResponseEntity.ok(bidService.buyout(user.getUserNo(), auctionId));
    }

}
