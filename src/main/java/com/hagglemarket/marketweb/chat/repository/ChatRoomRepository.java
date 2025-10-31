package com.hagglemarket.marketweb.chat.repository;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    //메서드 이름 기반 쿼리 파생
    //roomKind = k AND postId = postId AND seller.userNo = s AND buyer.userNo = b 인 채팅방을 단일 건으로 조회.
    //Seller_UserNo/Buyer_UserNo 처럼 _는 중첩 속성 탐색을 뜻함.
    //반환형 Optional<ChatRoom>: 없으면 Optional.empty()를 반환. (여러 건 나오면 예외 발생 -> 해당 조건이 유니크하다는 가정)
    //SELECT cr
    //FROM ChatRoom cr
    //WHERE cr.roomKind = :k
    //  AND cr.postId = :postId
    //  AND cr.seller.userNo = :s
    //  AND cr.buyer.userNo = :b
    Optional<ChatRoom> findByRoomKindAndPostIdAndSeller_UserNoAndBuyer_UserNo(RoomKind k, Integer postId, Integer s, Integer b);
    // roomKind = k AND auctionId = auctionId AND seller.userNo = s AND buyer.userNo= b인 채팅방을 단일 건으로 조회.
    //경매 글 기반의 1:1 채팅방을 찾을 때 사용.
    Optional<ChatRoom> findByRoomKindAndAuctionIdAndSeller_UserNoAndBuyer_UserNo(RoomKind k, Integer aucId, Integer s, Integer b);
    //roomKind = k AND orderId = orderId AND seller.userNo = s AND buyer.userNo = b
    //주문 기반의 채팅방을 찾을 때 사용
    Optional<ChatRoom> findByRoomKindAndOrderIdAndSeller_UserNoAndBuyer_UserNo(RoomKind k, Integer orderId, Integer s, Integer b);

    //내 방 목록 (판매자/구매자 둘 다 포함), 최신 활동순
    Page<ChatRoom> findBySeller_UserNoOrBuyer_UserNoOrderByUpdatedAtDesc(Integer sellerUserNo, Integer buyerUserNo, org.springframework.data.domain.Pageable pageable);

    //판매자/구매자 순서 무시하고, 두 유저 조합으로 잦기
    @Query("""
SELECT r FROM ChatRoom r
WHERE r.roomKind = :k
  AND (
    (r.seller.userNo = :u1 AND r.buyer.userNo = :u2)
    OR
    (r.seller.userNo = :u2 AND r.buyer.userNo = :u1)
  )
""")
    Optional<ChatRoom> findByRoomKindAndUserPair(RoomKind k, Integer u1, Integer u2);

    Optional<ChatRoom> findByRoomKindAndBuyer_UserNo(RoomKind kind, Integer buyerUserNo);
}
