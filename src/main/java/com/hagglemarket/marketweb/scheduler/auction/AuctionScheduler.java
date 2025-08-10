package com.hagglemarket.marketweb.scheduler.auction;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.entity.AuctionStatus;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.bid.entity.BidHistory;
import com.hagglemarket.marketweb.bid.repository.BidHistoryRepository;
import com.hagglemarket.marketweb.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
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
        List<AuctionPost> expiredAuctions = auctionPostRepository.findByEndTimeBeforeAndStatus(java.time.LocalDateTime.now(), AuctionStatus.ONGOING); //현재 시간이 지난(endTime < now) 아직 종료되지 않은 (closed = false) 경매글을 전부 가져옴

        for (AuctionPost auction : expiredAuctions) {   //종료되지 않은 경매들 반복 순회하면서 마감 처리함.
            log.info("[경매 마감 처리] 경매 ID: {}", auction.getAuctionId());

            //2.최고 입찰가 내역 가져오기
            //해당 경매글에 대한 모든 입찰 기록을 가져옴
            //나중에 낙찰자(가장 높은 금액을 입찰한 사람)를 고르기 위함.
            List<BidHistory> bids = bidHistoryRepository.findByAuctionPost(auction);

            if(!bids.isEmpty()){ //입찰가가 없는게 아니라면 실행
                //최고가 입찰자 찾기
                BidHistory topBid = bids.stream()       //Steam API를 사용해서 가장 높은 금액으로 입찰한 내역(BidHistory) 을 찾는 부분
                        .max(Comparator.comparingInt(BidHistory::getBidAmount)) //max()는 최대값을 구해주는 메서드고, 비교 기준은 BidAmount
                        .get();

//                bids.stream() → 리스트를 스트림으로 바꿈
//                Comparator.comparingInt(...) → 숫자 비교 기준을 설정
//                BidHistory::getBidAmount → 람다식으로 bid.getBidAmount()를 꺼냄
//                max() → 가장 큰 값을 가진 요소를 꺼냄


                //최고 입찰자의 유저 정보를 가져와서 AuctionPost의 낙찰자(winner)로 설정함.
                User winner = topBid.getBidder();
                auction.setWinner(winner);          //낙찰자 설정
            }

            //경매 상태를 ENDED로 변경
            auction.setStatus(AuctionStatus.ENDED);
            //이 변경된 경매글을 DB에 저장해서 반영.
            auctionPostRepository.save(auction);
        }
    }

    @Scheduled(fixedDelay = 60000) //1분마다 실행
    public void updateAuctionStatusToOngoing(){
        //List 형식으로 startTime이 지난 경매글들 불러오기
        List<AuctionPost> toStart = auctionPostRepository.findByStartTimeBeforeAndStatus(LocalDateTime.now(), AuctionStatus.READY);

        //List 애들 차례대로 AuctionStatus를 ONGOING 으로 변경
        for (AuctionPost auction : toStart) {
            auction.setStatus(AuctionStatus.ONGOING);
            auctionPostRepository.save(auction);    //변경 후 저장
            log.info("[경매 시작 처리] 경매 ID: {}", auction.getAuctionId());
        }
    }
}
