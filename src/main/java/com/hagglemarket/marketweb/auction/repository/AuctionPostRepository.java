package com.hagglemarket.marketweb.auction.repository;

import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//경매 상품 전용 리포지토리
//JpaRepository를 상속하면 CRUD 메서드 (save, findById 등)를 자동으로 제공함
//interface는 설계도 또는 기능 약속서 같은 거라고 생각하면 됨 / 클래스처럼 생겼지만, 실제 코드는 없고 "이런 메서드들이 있어야 해!"만 정의함
//밑 코드는 “AuctionPost를 저장하고 읽는 기능들을 내가 쓰겠다”는 선언
public interface AuctionPostRepository extends JpaRepository<AuctionPost, Integer> {


}
