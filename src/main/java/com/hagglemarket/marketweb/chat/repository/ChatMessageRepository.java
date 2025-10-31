package com.hagglemarket.marketweb.chat.repository;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

//ChatMessage 엔티티용 JPA 리포지토리. PK 타입은 Integer.
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    //파생쿼리. rood.id = :chatId 인 메시지를 id 내림차순으로 페이지네이션 조회.
    //room.id 처럼 Room_Id는 중첩 속성 탐색을 의미.
    //사용 예: 최신 메시지부터 PageRequest.of(0, 30)로 첫 페이지 로딩.
    Page<ChatMessage> findByRoom_IdOrderByIdDesc(Integer chatId, Pageable pageable);

    //같은 방에서 id < :beforeId 조건으로 더 오래된 메시지를 페이징.
    //흔히 키셋 페이지네이션(무한스크롤 "이전 더보기:)에 씀.
    //장점 : OFFSET 기반보다 성능/일관성 좋음(특히 큰 테이블에서).
    Page<ChatMessage> findByRoom_IdAndIdLessThanOrderByIdDesc(Integer roomId, Integer beforeId, Pageable pageable);

    //해당 방의 가장 최신 메시지 한 건 (TOP 1, ORDER BY id DESC)을 가져옴.
    //방 리스트에서 미리보기/ 마지막 메시지 표시, updatedAt 갱신 트리거 등에서 유용.
    //스프링 데이터는 findFirstBy... / findTop1By... 둘 다 지원.
    Optional<ChatMessage> findTop1ByRoom_IdOrderByIdDesc(Integer roomId);

    //네이티브 UPDATE로 chat_rooms.updated_at을 현재시각으로 갱신.
    //메시지 추가/읽음 등 이벤트 시 채팅방 최신 활동시간을 밀어올릴 때(정렬/목록 최신순) 호출.
    //@Modifying이 붙었으므로 트랜잭션 안에서 실행되어야 함(서비스 메서드에 @Transactional 권장).
    //권장 영속성 컨텍스트에 ChatRoom 엔티티가 이미 로드되어 있다면, 1차 캐시 값과 DB 값 불일치가 생길 수 있다.
    @Modifying @Query(value = "UPDATE chat_rooms SET updated_at = CURRENT_TIMESTAMP(3) WHERE chat_room_id=:roomId", nativeQuery = true)
    void touchRoomUpdatedAt(Integer roomId);
}

