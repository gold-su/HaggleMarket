package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service                    //  @Service: 스프링 서비스 빈 등록. 비즈니스 로직(썸네일 조회) 담당
@RequiredArgsConstructor    //  @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성(의존성 주입용)
@Slf4j
public class ThumbnailService {

    //경매 이미지 조회용 리포지토리 주입
    private final AuctionImageRepository imageRepo;

    @Transactional(readOnly = true) //읽기 전용 트랜잭션: 조회 성능 최적화
    public String getThumbnailUrl(int auctionId) {
        log.debug("[Thumbnail] resolve auctionId={}", auctionId);

        return imageRepo
                //특정 경매글의 이미들 중
                // sortOrder 오름차순 -> 같으면 imageId 오름차순으로 정렬하여 "첫 번째" 한 장만 Optional로 조회
                .findFirstByAuctionPost_AuctionIdOrderBySortOrderAscImageIdAsc(auctionId)
                //조회된 이미지의 imageId로 API 상대경로를 만들어 문자열로 변환
                .map(image -> {
                        String url ="/api/auction/images/" + image.getImageId();
                 log.debug("[Thumbnail] found imageId={} url={}", image.getImageId(), url);
                 return url;
                })
                .orElseGet(() -> {
                    log.warn("[Thumbnail] no image for auctionId={}", auctionId);
                    return null;
                });  //이미지가 없으면 null 반환
    }

    @Transactional(readOnly = true)
    public String getThumbnailUrl(AuctionPost post) {
        //오버로드 : 엔티티를 받으면 내부적으로 auctionId로 위 메서드 재사용
        return getThumbnailUrl(post.getAuctionId());
    }
}
