package com.hagglemarket.marketweb.auction.controller;

import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.auction.service.AuctionImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController //rest 컨트롤러로 명시
@RequestMapping("/api/auction/images")
@RequiredArgsConstructor //자동 생성자
public class AuctionImageController {

    private final AuctionImageService auctionImageService;
    private final AuctionPostRepository auctionPostRepository;

    //이미지 업로드 API
    @PostMapping("/{auctionId}") //경로 변수
    public ResponseEntity<String> uploadImages(
            @PathVariable("auctionId") int auctionId,   //Url 경로에서 {auctionId} 값을 가져와서 Java 메서드의 auctionId 매개변수로 바인딩. 즉, 요청 경로의 숫자를 int auctionId 변수에 전달
            @RequestPart("images") List<MultipartFile> images // multipart/form-data 요청에서 파일 파트를 분리해서 받기 위한 애노테이션. images는 form-data의 키 이름과 매칭
    ) {
        //경매 상품 조회
        AuctionPost auctionPost = auctionPostRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다."));

        //이미지 저장
        List<AuctionImage> savedImages = auctionImageService.saveImages(images, auctionPost);

        //응답 반환
        return ResponseEntity.ok(savedImages.size()+ "개의 이미지 업로드 완료");
    }
}
