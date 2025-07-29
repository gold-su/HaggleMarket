package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.dto.AuctionDetailDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionPostRequest;
import com.hagglemarket.marketweb.auction.dto.AuctionPostResponse;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.List;

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

    @Transactional //읽기 전용
    public AuctionDetailDTO getAuctionDetail(int auctionId){

        AuctionPost post = auctionPostRepository.findById(auctionId)  //auctionId로 DB 에서 경매 글 조회
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다.")); //없으면 예외 던져서 400 에러 유도

        //경매 게시글에 연결된 이미지 리스트를 꺼내서 각각의 이미지에서 imageName만 뽑아낸 뒤 리스트로 만듦.
        List<String> imageNames = post.getImages().stream()
                .map(AuctionImage::getImageName)
                .toList();

        //DTO를 builder 패턴으로 생성
        return AuctionDetailDTO.builder()
                .auctionId(post.getAuctionId())
                .title(post.getTitle())
                .content(post.getContent())
                .startPrice(post.getStartCost())
                .currentPrice(post.getCurrentCost())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .imagesName(imageNames)
                .sellerNickname(post.getSeller().getNickName())
                .winnerNickname(post.getWinner() == null ? null : post.getWinner().getNickName()) //null일 수 있음
                .build();
    }
}
