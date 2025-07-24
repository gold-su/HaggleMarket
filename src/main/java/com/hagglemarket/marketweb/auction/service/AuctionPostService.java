package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.dto.AuctionPostRequest;
import com.hagglemarket.marketweb.auction.dto.AuctionPostResponse;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.ResourceTransactionManager;

@Service //스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor //생성자 자동 주입
public class AuctionPostService {

    private final AuctionPostRepository auctionPostRepository;
    private final UserRepository userRepository;

    //경매 상품 등록 메서드
    @Transactional
    public AuctionPostResponse createAuctionPost(AuctionPostRequest request, Integer userNo) {

        //판매자 조회
        User seller = userRepository.findById(userNo)
                .orElseThrow(()-> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        //AuctionPost 객체 생성 및 값 설정
        AuctionPost post = new AuctionPost();
        post.setSeller(seller);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setStartCost(request.getStartCost());
        post.setCurrentCost(request.getStartCost()); //현재가 = 시작가
        post.setBuyoutCost(request.getBuyoutCost());
        post.setStartTime(request.getStartTime());
        post.setEndTime(request.getEndTime());

        //DB에 저장
        auctionPostRepository.save(post);

        //응답 반환
        return new AuctionPostResponse(post.getAuctionId(), "경매 상품이 등록되었습니다.");

    }
}
