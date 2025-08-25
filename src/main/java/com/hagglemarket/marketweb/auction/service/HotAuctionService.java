package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.dto.HotAuctionItemDTO;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.bid.repository.AuctionBidCount;
import com.hagglemarket.marketweb.bid.repository.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotAuctionService {

    private final BidHistoryRepository bidRepo;         //입찰 이력 쿼리(인기 순 집계 등)
    private final AuctionPostRepository postRepo;       //경매글 본문/메타 조회
    private final ThumbnailService thumbnailService;    //대표 이미지(썸네일) 조회

    @Transactional(readOnly = true) //읽기 전용 트랜잭션
    public Page<HotAuctionItemDTO> getHot(Pageable pageable){
        //인기 경매 ID 페이지 조회(입찰수 기준 랭킹). AuctionBidCount는 (auctionId, bidCount) 프로잭션
        Page<AuctionBidCount> rankPage = bidRepo.findHotAuctionIds(pageable);

        //현재 페이지에 포함된 경매글 ID만 추출
        List<Integer> ids = rankPage.getContent().stream()
                .map(AuctionBidCount::getAuctionId)
                .toList();

        //경매글 상세를 한 번에 가져와 Map<auctionId, AuctionPost>로 구성(순서 보존은 아래 for 에서 처리)
        Map<Integer, AuctionPost> postMap = postRepo.findByAuctionIdIn(ids).stream()
                .collect(Collectors.toMap(AuctionPost::getAuctionId, Function.identity()));

        List<HotAuctionItemDTO> content = new ArrayList<>();
        //랭킹 페이지의 순서를 유지하며 순회
        for(AuctionBidCount r : rankPage.getContent()){
            AuctionPost ap = postMap.get(r.getAuctionId());
            if(ap == null) continue; //혹시 삭제/비공개 된 경우 방어

            //각 경매글의 대표 이미지 URL 조회
            String thumbnailUrl = thumbnailService.getThumbnailUrl(ap.getAuctionId());

            content.add(HotAuctionItemDTO.builder()
                            .auctionId(r.getAuctionId())        //경매 ID
                            .bidCount(r.getBidCount())          //입찰 수
                            .title(ap.getTitle())               //제목
                            .thumbnailUrl(thumbnailUrl)         //썸네일 url
                            .currentPrice(ap.getCurrentCost())  //현재가
                            .endTime(ap.getEndTime())           //종료 시각
                            .status(ap.getStatus().name())      //enum -> 문자열 코드
                            .build());
        }

        //원래 집계 페이지의 totalElements를 유지한 채 DTO 페이지로 감싸서 반환
        return new PageImpl<>(content, pageable, rankPage.getTotalElements());
    }
}
