package com.hagglemarket.marketweb.bid.service;

import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.entity.AuctionStatus;
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

        int bidderUserNo = request.getUserNo();

        //경매글 조회
        AuctionPost auction = auctionPostRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("경매 상품이 존재하지 않습니다."));

        //시간 검증
        var now = java.time.LocalDateTime.now();
        if(now.isBefore(auction.getStartTime())){
            return BidResponseDTO.builder()
                    .success(false).message("경매가 아직 시작되지 않았습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }
        if(now.isAfter(auction.getEndTime())){
            return BidResponseDTO.builder()
                    .success(false).message("경매가 종료되었습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }
        //본인 물건 입찰 방지
        if(auction.getSeller().getUserNo() == bidderUserNo){
            return BidResponseDTO.builder()
                    .success(false).message("본인 상품에는 입찰할 수 없습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }

        int current = auction.getCurrentCost();
        int amount  = request.getBidAmount();


        if(amount <= current){
            return BidResponseDTO.builder()
                    .success(false).message("입찰 금액은 현재가보다 높아야 합니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }


        //입찰자 조회
        User bidder = userRepository.findByUserNo(bidderUserNo)
                .orElseThrow(() -> new IllegalArgumentException("입찰 유저가 존재하지 않습니다."));

        //입찰 기록 저장 (누가, 언제, 얼마로 입찰했는지 입찰 이력 기록)
        BidHistory bid = new BidHistory(); //bid를 BidHistory 엔티티로 생성
        bid.setAuctionPost(auction);                //입찰 경매 상품 저장
        bid.setBidder(bidder);                      //입찰자 저장
        bid.setBidAmount(amount);                   //입찰 금액
        bid.setBidTime(now);                        //입찰 현재 시간 적용

        bidHistoryRepository.save(bid);             //JpaRepository 상속하였으니 불러와서 바로 save로 저장

        //경매글의 현재가/낙찰자/입찰 수 갱신 (현재가, 낙찰자, 입찰 수 등의 경매 글 상태 갱신)
        auction.setCurrentCost(amount);                 //현재가 갱신
        auction.setWinner(bidder);                      //숫자 필드에 일관 저장 낙찰자 갱신
        auction.setBidCount(auction.getBidCount() + 1); //입찰 수 +1
        auction.setUpdatedAt(now);
        auctionPostRepository.save(auction);            //후 저장

        //성공 응답 반환
        return BidResponseDTO.builder()
                .success(true)
                .message("입찰이 성공적으로 완료되었습니다.")
                .currentHighestBid(amount)
                .build();

    }
    @Transactional
    public BidResponseDTO  buyout(final int buyerUserNo, final int auctionId) {
        AuctionPost auction = auctionPostRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("경매 상품이 존재하지 않습니다."));
        var now = java.time.LocalDateTime.now();
        if (now.isBefore(auction.getStartTime())) {
            return BidResponseDTO.builder()
                    .success(false).message("경매가 아직 시작되지 않았습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }
        if (now.isAfter(auction.getEndTime())) {
            return BidResponseDTO.builder()
                    .success(false).message("경매가 종료되었습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }
        if (auction.getBuyoutCost() == null) {
            return BidResponseDTO.builder()
                    .success(false).message("즉시구매가가 설정되지 않은 상품입니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }
        if (auction.getSeller().getUserNo() == buyerUserNo) {
            return BidResponseDTO.builder()
                    .success(false).message("본인 상품은 구매할 수 없습니다.")
                    .currentHighestBid(auction.getCurrentCost())
                    .build();
        }


        User buyer = userRepository.findByUserNo(buyerUserNo)
                .orElseThrow(() -> new IllegalArgumentException("구매 유저가 존재하지 않습니다."));


        //종료 처리
        auction.setCurrentCost(auction.getBuyoutCost());
        auction.setWinner(buyer);
        auction.setEndTime(now);
        auction.setStatus(AuctionStatus.ENDED);
        auction.setUpdatedAt(now);
        auctionPostRepository.save(auction);

        return BidResponseDTO.builder()
                .success(true)
                .message("즉시구매가 완료되었습니다.")
                .currentHighestBid(auction.getBuyoutCost())
                .build();
    }
}
