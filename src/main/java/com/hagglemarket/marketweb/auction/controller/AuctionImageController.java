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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //rest 컨트롤러로 명시
@RequestMapping("/api/auction/images")
@RequiredArgsConstructor //자동 생성자
public class AuctionImageController {

    private final AuctionImageService auctionImageService;
    private final AuctionPostRepository auctionPostRepository;
    private final AuctionImageRepository auctionImageRepository;

    //이미지 업로드 API
    @PostMapping(value = "/{auctionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //경로 변수, MULTIPART_FORM_DATA_VALUE = 파일 업로드 폼만 받겠다는 의미
    public ResponseEntity<AuctionImageResponseDto> uploadImages(
            @PathVariable int auctionId,   //경로변수를 메서드 파라미터로 매핑. URL 경로에 {auctionId}라는 자리표시자가 있으면, 그 값을 받아옴
            @ModelAttribute @Valid AuctionImageRequestDto request
            //ModelAttribute 는 HTTP 요청 데이터(폼 필드, 쿼리 파라미터, multipart/form-data 본문 등)를 자바 객체(DTO)에 바인딩.
            //여기서는 파일 업로드용 multipart/form-data 요청을 처리하므로, images 같은 필드가 MultipartFile 리스트로 자동 매핑된다.
            //즉, HTML form 에서 전송한 name= "images" 파일들의 DTO의 images 필드에 들어가게 됨.
            //@Valid 는 DTO에 선언된 검증 애노테이션을 실행 해준다.
            //AuctionImageRequestDto 는 업로드 요청 데이터를 담는 DTO 클래스이다.
    ) {
        //경매 상품 조회
        AuctionPost auctionPost = auctionPostRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다."));

//        //이미지 저장
//        List<AuctionImage> savedImages = auctionImageService.saveImages(images, auctionPost);

        //saveImages(업로드된 이미지 파일, 경매 게시글 엔티티)
        //서비스 메서드 내부에서 하는 일:
        //request.getImages()로 파일 목록 가져옴.
        //각 파일을 AuctionImage 엔티티로 변환해 DB 저장.
        //저장된 이미지 정보를 AuctionImageResponseDto.ImageInfo 리스트로 반환.
        var saved = auctionImageService.saveImages(request, auctionPost);
        //응답 반환 (저장된 이미지 개수, 이미지 정보 리스트)로 응다 DTO 객체 생성
        return ResponseEntity.ok(new AuctionImageResponseDto(saved.size(), saved));
    }

    //이미지 바이트 조회 (썸네일)
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getThumbnailImage(@PathVariable int imageId) {
        AuctionImage img = auctionImageRepository.findById(imageId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "이미지 없음"));

        String type = (img.getImageType() == null || img.getImageType().isBlank())
                ? "image/jpeg" : img.getImageType();

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(type))
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic())
                .body(img.getImageData());
    }
}
