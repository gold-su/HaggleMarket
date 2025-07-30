package com.hagglemarket.marketweb.bid.controller;

import com.hagglemarket.marketweb.bid.dto.BidRequestDTO;
import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import com.hagglemarket.marketweb.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //json 반환 컨트롤러
@RequiredArgsConstructor  //final로 선언된 필드 기반 생성자 자동 생성 (DI 주입용)
@RequestMapping("/api/bids") //공옽 URI 설정
public class BidController {

    private final BidService bidService;

    //입찰 요청
    @PostMapping
    public ResponseEntity<BidResponseDTO> placeBid(@RequestBody final BidRequestDTO request) {
        BidResponseDTO bidResponseDTO = bidService.placeBid(request);   //서비스에서 로직 처리
        return ResponseEntity.ok(bidResponseDTO); //상태 200 0k와 함께 응답 DTO 반환
    }

}
