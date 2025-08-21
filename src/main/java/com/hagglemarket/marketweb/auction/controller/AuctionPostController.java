package com.hagglemarket.marketweb.auction.controller;

import com.hagglemarket.marketweb.auction.dto.AuctionDetailDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionListDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionPostRequest;
import com.hagglemarket.marketweb.auction.dto.AuctionPostResponse;
import com.hagglemarket.marketweb.auction.service.AuctionPostService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //REST API 컨트롤러
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class AuctionPostController {

    private final AuctionPostService auctionPostService;

    //경매 상품 등록 API
    @PostMapping("/create")
    public ResponseEntity<AuctionPostResponse> createAuctionPost(
            @RequestBody @Valid AuctionPostRequest request,
            @AuthenticationPrincipal CustomUserDetails user // Spring Security
    ) {
        if (user == null) {
            // 토큰 안 붙었거나, 경로가 인증 요구가 아닌 경우
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        int userNo = user.getUserNo();
        //서비스 호출
        AuctionPostResponse response = auctionPostService.createAuctionPost(request, userNo);

        //200 OK 응답
        return ResponseEntity.ok(response);
    }


    //메인 페이지 전체 경매 리스트 (썸네일)
    @GetMapping("/list")
    public ResponseEntity<List<AuctionListDTO>> getAuctionList(){  //리턴값은 List 형식인 AuctionDTO
        List<AuctionListDTO> list = auctionPostService.getAuctionList(); //auctionPostService 에서 getAuctionList 를 불러와서 list 로 저장
        return ResponseEntity.ok(list); //저장된 list 반환
    }

    //상세보기 페이지 특정 경매 상세 조회
    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionDetailDTO> getAuctionDetail(@PathVariable int auctionId){ //리턴값은 AuctionDetailDTO 이고 Path 값을 auctionId
        AuctionDetailDTO detail = auctionPostService.getAuctionDetail(auctionId); //똑같이 서비스에서 디테일에 auctionId 값을 받아와서 detail로 저장
        return ResponseEntity.ok(detail); //저장된 디테일 반환
    }

    @PutMapping("/{auctionId}")
    public ResponseEntity<AuctionPostResponse> updateAuctionPost(
            @PathVariable int auctionId,
            @RequestBody @Valid AuctionPostRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        if(user == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,"로그인이 필요합니다."
            );
        }

        AuctionPostResponse response =
                auctionPostService.updateAuctionPost(auctionId, request, user.getUserNo());

        return ResponseEntity.ok(response);

    }

}
