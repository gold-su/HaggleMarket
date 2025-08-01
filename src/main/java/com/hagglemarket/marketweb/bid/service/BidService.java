package com.hagglemarket.marketweb.bid.service;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.bid.dto.BidRequestDTO;
import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import com.hagglemarket.marketweb.bid.entity.BidHistory;
import com.hagglemarket.marketweb.bid.repository.BidHistoryRepository;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionPostRepository auctionPostRepository;      //옥션 post 레포 가져오기
    private final BidHistoryRepository bidHistoryRepository;        //입찰가 높은 순 레포 가쟈오기
    private final UserRepository userRepository;

    @Transactional
    public BidResponseDTO placeBid(final BidRequestDTO request) {
        //1. 경매글 조회
        AuctionPost auction = auctionPostRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("경매 상품이 존재하지 않습니다."));

        //2. 경매 종료 여부 확인
        if(auction.getEndTime().isBefore(LocalDateTime.now())){  //현재 시간이 EndTime 보다 이후 라면
            return BidResponseDTO.builder()                      //경매 종료 DTO 반환
                    .success(false)                              //성공 여부 실패
                    .message("경매가 종료되었습니다.")               //메시지 전달
                    .currentHighestBid(auction.getCurrentCost()) //현재 최고 입찰가 표시
                    .build();
        }

        //3. 입찰 금액이 현재가보다 높은지 확인
        if(request.getBidAmount() <= auction.getCurrentCost()){  //현재 가격보다 입찰 금액이 높은지 비교
            return BidResponseDTO.builder()                      //경매 종료 DTO 반환
                    .success(false)                              //성공 여부 실패
                    .message("입찰 금액은 현재가보다 높아야 합니다.")  //메시지 전달
                    .currentHighestBid(auction.getCurrentCost()) //현재 최고 입찰가 표시
                    .build();
        }

        //4. 입찰자 조회
        User bidder = userRepository.findByUserNo(request.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("입찰 유저가 존재하지 않습니다."));

        //5. 입찰 기록 저장 (누가, 언제, 얼마로 입찰했는지 입찰 이력 기록)
        BidHistory bid = new BidHistory(); //bid를 BidHistory 엔티티로 생성
        bid.setAuctionPost(auction);                //입찰 경매 상품 저장
        bid.setBidder(bidder);                      //입찰자 저장
        bid.setBidAmount(request.getBidAmount());   //입찰 금액 저장
        bid.setBidTime(LocalDateTime.now());        //입찰 현재 시간 적용

        bidHistoryRepository.save(bid);             //JpaRepository 상속하였으니 불러와서 바로 save로 저장

        //6. 경매글의 현재가/낙찰자/입찰 수 갱신 (현재가, 낙찰자, 입찰 수 등의 경매 글 상태 갱신)
        auction.setCurrentCost(request.getBidAmount()); //현재가 갱신
        auction.setWinner(bidder);                      //낙찰자 갱신
        auction.setBidCount(auction.getBidCount() + 1); //입찰 수 +1 갱신
        auctionPostRepository.save(auction);            //후 저장

        //7. 성공 응답 반환
        return BidResponseDTO.builder()
                .success(true)
                .message("입찰이 성공적으로 완료되었습니다.")
                .currentHighestBid(request.getBidAmount())
                .build();

    }

}
