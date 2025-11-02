package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.dto.AuctionDetailDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionListDTO;
import com.hagglemarket.marketweb.auction.dto.AuctionPostRequest;
import com.hagglemarket.marketweb.auction.dto.AuctionPostResponse;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionPostRepository;
import com.hagglemarket.marketweb.category.repository.CategoryRepository;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hagglemarket.marketweb.category.entity.Category;

import java.util.Comparator;
import java.util.List;

@Service //스프링이 이 클래스를 서비스 빈으로 등록
@RequiredArgsConstructor //생성자 자동 주입
public class AuctionPostService {

    private final AuctionPostRepository auctionPostRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

        if (request.getCategoryId() != null) {
            //카테고리 존재 여부 확인
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + request.getCategoryId()));
            post.setCategory(request.getCategoryId());
        }

        //DB에 저장
        auctionPostRepository.save(post);

        //응답 반환
        return new AuctionPostResponse(post.getAuctionId(), "경매 상품이 등록되었습니다.");

    }


    public List<AuctionListDTO> getAuctionList() {
        // 1️⃣ 종료되지 않은 경매 (종료 임박 순)
        List<AuctionPost> ongoing = auctionPostRepository.findOngoingSortedByEndTime();

        // 2️⃣ 종료된 경매 (최근 종료 순)
        List<AuctionPost> ended = auctionPostRepository.findEndedSorted();

        // 3️⃣ 두 리스트 병합
        List<AuctionPost> all = new java.util.ArrayList<>();
        all.addAll(ongoing);
        all.addAll(ended);

        // 4️⃣ DTO 변환
        return all.stream().map(post -> {
            String thumbnailUrl = null;

            if (!post.getImages().isEmpty()) {
                int firstImageId = post.getImages()
                        .stream()
                        .min(Comparator.comparingInt(AuctionImage::getSortOrder))
                        .get()
                        .getImageId();
                thumbnailUrl = "/api/auction/images/" + firstImageId;
            }

            return AuctionListDTO.builder()
                    .id(post.getAuctionId())
                    .title(post.getTitle())
                    .thumbnailUrl(thumbnailUrl)
                    .currentPrice(post.getCurrentCost())
                    .endTime(post.getEndTime())
                    .hit(post.getHit())
                    .bidCount(post.getBidCount())
                    .build();
        }).toList();
    }

    @Transactional//쓰기 가능 트랜잭션 / 읽기 전용도 가능 ( 기본값은 false )
    public AuctionDetailDTO getAuctionDetail(int auctionId){

        AuctionPost post = auctionPostRepository.findById(auctionId)  //auctionId로 DB 에서 경매 글 조회
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다.")); //없으면 예외 던져서 400 에러 유도

        //경매 게시글에 연결된 이미지 리스트를 꺼내서 각각의 이미지에서 imageName만 뽑아낸 뒤 리스트로 만듦.
        List<String> imageUrls = post.getImages().stream()
                .sorted(Comparator.comparingInt(AuctionImage::getSortOrder))
                .map(img -> "/api/auction/images/" + img.getImageId())
                .toList();

        //getAuctionDetail() 호출 시 +1
        post.setHit(post.getHit() + 1);
        auctionPostRepository.save(post);

        //카테고리 경로 구성
        String categoryPath = null;
        List<Integer> categoryIds = null;
        Integer smallId = post.getCategory(); // ⬅️ AuctionPost에 소분류 id가 있다고 가정
        if (smallId != null) {
            Category small = categoryRepository.findById(smallId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + smallId));
            Category middle = small.getParent();
            Category large  = (middle != null ? middle.getParent() : null);

            // 이름들 조립 (부모가 null일 수 있는 환경도 방어)
            String largeName  = (large  != null ? large.getName()  : null);
            String middleName = (middle != null ? middle.getName() : null);
            String smallName  = small.getName();

            // "대 > 중 > 소" 형태로 안전하게 생성
            if (largeName != null && middleName != null) {
                categoryPath = largeName + " > " + middleName + " > " + smallName;
                categoryIds  = List.of(large.getId(), middle.getId(), small.getId());
            } else if (middleName != null) {
                categoryPath = middleName + " > " + smallName;
                categoryIds  = List.of(middle.getId(), small.getId());
            } else {
                categoryPath = smallName;
                categoryIds  = List.of(small.getId());
            }
        }

        //판매자 정보가 null이 아닌지 확인 후 값 채우기
        var seller = post.getSeller();
        Integer sellerUserId = null;
        String sellerNickname = null;
        String sellerAddress = null;
        String sellerProfileImageUrl = null;

        if(seller != null) {
            sellerUserId = seller.getUserNo();
            sellerNickname = seller.getNickName();
            sellerAddress = seller.getAddress();
            sellerProfileImageUrl = seller.getImageURL();
        }

        //DTO를 builder 패턴으로 생성
        return AuctionDetailDTO.builder()
                .auctionId(post.getAuctionId())
                .title(post.getTitle())
                .content(post.getContent())
                .startPrice(post.getStartCost())
                .currentPrice(post.getCurrentCost())
                .buyoutPrice(post.getBuyoutCost())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .imagesUrls(imageUrls)

                //판매자 정보
                .sellerUserId(sellerUserId)
                .sellerNickname(sellerNickname)
                .sellerAddress(sellerAddress)
                .sellerProfileImageUrl(sellerProfileImageUrl)

                .sellerNickname(post.getSeller().getNickName())

                //낙찰자 정보
                .winnerNickname(post.getWinner() == null ? null : post.getWinner().getNickName()) //null일 수 있음

                .hit(post.getHit())
                .bidCount(post.getBidCount())
                .categoryId(smallId)
                .categoryIds(categoryIds)
                .categoryPath(categoryPath)
                .build();
    }

    @Transactional
    public AuctionPostResponse updateAuctionPost(int auctionId,AuctionPostRequest req, Integer userNo) {
        AuctionPost post = auctionPostRepository.findById(auctionId)
                .orElseThrow(()->new IllegalArgumentException("해당 경매 상품이 존재하지 않습니다."));

        // 1) 권한 체크: 본인만 수정 가능
        if (post.getSeller() == null || post.getSeller().getUserNo() != userNo) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "본인 게시글만 수정할 수 있습니다.");
        }

        // 2) 상태/입찰 체크: 입찰자 있으면 수정 불가
        if (post.getBidCount() > 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "입찰자가 있어 수정할 수 없습니다.");
        }

        // (선택) READY 상태가 아닐 때도 수정 불가로 고정
        if (post.getStatus() != com.hagglemarket.marketweb.auction.entity.AuctionStatus.READY) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "시작되었거나 종료된 경매는 수정할 수 없습니다.");
        }

        // 3) 즉시구매가 검증: 현재가보다 낮게 설정 금지
        if (req.getBuyoutCost() != null) {
            if (req.getBuyoutCost() < post.getCurrentCost()) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "즉시구매가는 현재가보다 낮을 수 없습니다.");
            }
        }

        // 4) 시간 검증 (둘 다 들어온 경우에만 검사)
        // startTime, endTime은 READY 상태에서만 바꾸는 걸 권장
        var newStart = req.getStartTime() != null ? req.getStartTime() : post.getStartTime();
        var newEnd = req.getEndTime() != null ? req.getEndTime()       : post.getEndTime();

        if(newStart != null && newEnd != null && !newStart.isBefore(newEnd)){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "시작 시간은 종료 시간보다 이전이어야 합니다."
            );
        }

        // 과거 시간 검증(선택) : 이제 시작 전인 경매만 허용
        var now = java.time.LocalDateTime.now();
        if(newStart != null && newStart.isBefore(now)){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "시작 시간은 현재 이후여야 합니다."
            );
        }
        if(newEnd != null && newEnd.isBefore(now)){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "종료 시간은 현재 이후여야 합니다."
            );
        }

        // 5) 변경 적용 (null은 무시)
        if(req.getTitle() != null) post.setTitle(req.getTitle());
        if(req.getContent() != null) post.setContent(req.getContent());
        if (req.getBuyoutCost() != null || (req.getBuyoutCost() == null)) {
            post.setBuyoutCost(req.getBuyoutCost()); // null 이면 ‘즉시구매 불가’
        }
        if (req.getStartTime() != null) post.setStartTime(newStart);
        if (req.getEndTime() != null)   post.setEndTime(newEnd);

        post.setUpdatedAt(java.time.LocalDateTime.now());

        auctionPostRepository.save(post);

        return new AuctionPostResponse(post.getAuctionId(), "경매 상품이 수정되었습니다.");

    }
}
