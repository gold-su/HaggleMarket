package com.hagglemarket.marketweb.bid.controller;

import com.hagglemarket.marketweb.bid.dto.BidRequestDTO;
import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import com.hagglemarket.marketweb.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//한 줄 흐름 요약
// * POST /api/auction/{auctionId}/bid
//   인증 사용자 확인 → userNo/auctionId를 서버에서 강제 주입 → bidService.placeBid() 실행 → 200 OK.
// * POST /api/auction/{auctionId}/buyout
//   인증 사용자 확인 → bidService.buyout(userNo, auctionId) 실행 → 200 OK.


@RestController //json 반환 컨트롤러
@RequiredArgsConstructor  //final로 선언된 필드 기반 생성자 자동 생성 (DI 주입용)
@RequestMapping("/api/auction") //공통 URI 설정
public class BidController {

    private final BidService bidService; //서비스 의존성 : 생성자 주입 대상

    //입찰 요청
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<BidResponseDTO> placeBid(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.hagglemarket.marketweb.security.CustomUserDetails user,
            //SecurityContext의 인증 사용자 주입(커스텀 UserDetails)
            @PathVariable int auctionId,
            //경로변수로 경매글 ID 수신
            @RequestBody BidRequestDTO request
            //요청 본문으로 입찰 금액 등 수신
    ) {
        if(user == null){ //비인증 시 예외로 401 발생(메시지 포함)
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."
            );

        }
        //바디에서 auctionId 받지 않고 path를 신뢰 (혼선을 막기 위해 통일)
        request.setUserNo(user.getUserNo()); //서버 측에서 인증된 userNo를 주입하여 신뢰 경로로 강제(클라이언트 변조 방지)
        request.setAuctionId(auctionId);     //경로변수의 auctionId를 요청 DTO에 주입(바디의 auctionId 무시)
        return ResponseEntity.ok(bidService.placeBid(request)); //비즈니스 로직 위임 후 200 OK + BidResponseDTO 반환
    }

    @PostMapping("/{auctionId}/buyout")
    public ResponseEntity<BidResponseDTO> buyout(
            @PathVariable int auctionId, //즉시구매 대상 경매 ID
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.hagglemarket.marketweb.security.CustomUserDetails user
            //인증 사용자 정보
    ){
        if(user == null){ //비인증 시 예외로 401 발생(메시지 포함)
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."
            );
        }
        return ResponseEntity.ok(bidService.buyout(user.getUserNo(), auctionId));   //즉시구매 로직 실행 후 200 OK + 응답 DTO
    }

}
