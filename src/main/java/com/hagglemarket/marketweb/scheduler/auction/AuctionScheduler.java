package com.hagglemarket.marketweb.scheduler.auction;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.bid.repository.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component //이 클래스를 Spring Bean 으로 등록해서 자동으로 실행되게 해줌
@Slf4j     //로그 찍을 수 있게 도와주는 Lombok 애노테이션 (log.info() 사용 가능)
@RequiredArgsConstructor //final로 선언된 의존성(리포지토리들)을 자동 생성자 주입하게 해줌.
public class AuctionScheduler { //클래스 이름은 보통 Scheduler 또는 Job 으로 끝나게 짓는 게 일반적임.

    private final AuctionPostRepository auctionPostRepository;  //경매글 리포 가져오기
    private final BidHistoryRepository bidHistoryRepository;    //입찰 내역 리포 가져오기

    @Scheduled(fixedDelay = 60000) //1분마다 이 메서드를 반복 실행
    public void closeExpiredAuctions(){ //만료된 경매 종료

        //1. 마감 시간이 현재 시간보다 지난 경매글들 조회
        List<AuctionPost> expiredAuctions = auctionPostRepository.findByEndTimeBeforeAndClosedFalse(java.time.LocalDateTime.now()); //현재 시간이 지난(endTime < now) 아직 종료되지 않은 (closed = false) 경매글을 전부 가져옴

        for (AuctionPost auction : expiredAuctions) {   //종료되지 않은 경매들 반복 순회하면서 마감 처리함.
            log.info("[경매 마감 처리] 경매 ID: {}", auction.getAuctionId());

        }
    }
}
