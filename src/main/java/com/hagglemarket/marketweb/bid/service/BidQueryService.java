package com.hagglemarket.marketweb.bid.service;

import com.hagglemarket.marketweb.bid.dto.BidHistoryItemDTO;
import com.hagglemarket.marketweb.bid.dto.BidHistoryMapper;
import com.hagglemarket.marketweb.bid.repository.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //final 필드 생성자 주입
public class BidQueryService {

    //리포지토리 의존성: 생성자 주입 대상으로 final 선언
    private final BidHistoryRepository repo;

    @Transactional(readOnly = true) //읽기 전용 트랜잭션 : 성능 최적화
    public Page<BidHistoryItemDTO> getBidHistory(int auctionId, Pageable pageable) {
        return repo.findPageByAuctionId(auctionId, pageable) //특정 경매글의 입찰 이력을 페이지 단위로 조회
                .map(BidHistoryMapper::toDto);               //Page<엔티티> -> Page<DTO> 변환 : 각 요소에 매퍼 적용
    }

    @Transactional
    public long count(int auctionId){   //읽기 전용 트랜잭션으로 카운트 쿼리 실행
        return repo.countByAuctionPost_AuctionId(auctionId); //연관 경로 기반 메서드 네이밍 : AuctionPost.auctionId로 count
    }
}
