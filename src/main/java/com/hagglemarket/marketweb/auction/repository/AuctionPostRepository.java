package com.hagglemarket.marketweb.auction.repository;

import com.hagglemarket.marketweb.auction.dto.AuctionListDTO;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//경매 상품 전용 리포지토리
//JpaRepository를 상속하면 CRUD 메서드 (save, findById 등)를 자동으로 제공함
//interface는 설계도 또는 기능 약속서 같은 거라고 생각하면 됨 / 클래스처럼 생겼지만, 실제 코드는 없고 "이런 메서드들이 있어야 해!"만 정의함
//밑 코드는 “AuctionPost를 저장하고 읽는 기능들을 내가 쓰겠다”는 선언
public interface AuctionPostRepository extends JpaRepository<AuctionPost, Integer> {

    @Query("SELECT new com.hagglemarket.marketweb.auction.dto.AuctionListDTO(" +   //필드를 조회해서 DTO로 직접 반환하는 방식
            "p.auctionId, p.title, i.imageName, p.currentCost, p.endTime)" +
            "FROM AuctionPost p " +         //AuctionPost를 기준으로 조회 (엔티티 이름 기준)
            "LEFT JOIN p.images i with i.sortOrder = 1 " +    //AUctionPost와 연결된 images 중에 sortOrder == 1인 이미지만 조인 / 1은 첫번째 이미지
            "ORDER BY p.createdAt DESC")                    //ORDER BY p.createdAt DESC : 최신 등록된 게시물이 먼저 보이도록 정렬
    List<AuctionListDTO> findAllWithThumbnail();
}
