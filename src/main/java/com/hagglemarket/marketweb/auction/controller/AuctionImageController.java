package com.hagglemarket.marketweb.auction.controller;

import com.hagglemarket.marketweb.auction.dto.AuctionImageRequestDto;
import com.hagglemarket.marketweb.auction.dto.AuctionImageResponseDto;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionImageRepository;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.auction.service.AuctionImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    private final AuctionImageRepository auctionImageRepository;

    //이미지 업로드 API
    @PostMapping(value = "/{auctionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //경로 변수
    public ResponseEntity<AuctionImageResponseDto> uploadImages(
            @PathVariable int auctionId,   //
            @ModelAttribute @Valid AuctionImageRequestDto request //
    ) {
        //경매 상품 조회
        AuctionPost auctionPost = auctionPostRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다."));

//        //이미지 저장
//        List<AuctionImage> savedImages = auctionImageService.saveImages(images, auctionPost);

        var saved = auctionImageService.saveImages(request, auctionPost);
        //응답 반환
        return ResponseEntity.ok(new AuctionImageResponseDto(saved.size(), saved));
    }

    //이미지 바이트 조회 (썸네일)
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getThumbnailImage(@PathVariable int imageId) {
        AuctionImage img = auctionImageRepository.findById(imageId)
                .orElseThrow(()->new IllegalArgumentException("이미지 없음"));
        return ResponseEntity.ok()
                .header("Content-Type", img.getImageType())
                .body(img.getImageData());
    }
}
