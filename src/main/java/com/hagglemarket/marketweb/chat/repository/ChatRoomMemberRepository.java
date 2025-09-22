package com.hagglemarket.marketweb.chat.repository;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoomMember;
import com.hagglemarket.marketweb.chat.domain.id.ChatRoomMemberId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

//이 리포지토리는 채팅방 멤버 행을 다룬다.
//advanceCursor(...)는 해당 유저의 "읽음 커서(last_read_message_id)"를 앞으로만(증가 방향으로만) 이동시키는 네이티브 UPDATE.
//이미 더 앞(큰 id)를 가리키면 그대로 유지하고, 비어 있거나 뒤에 있으면 seen으로 갱신함.
//반환값 int는 영향받은 행 수(보통 0 또는 1) 임.
//레포지토리 인터페이스 선언
//제네릭 <엔티티, PK타입> -> chatRoomMember와 복합키 ChatRoomMemberId.
//기본 CRUD(save, findById 등) 자동 제공.
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, ChatRoomMemberId> {

    @Modifying //이 메서드가 데이터 변경(UPDATE/DELETE) 쿼리임을 선언. 트랜잭션 필요 : 서비스에 @Transactional을 붙여 호출.
    //네이티브 SQL 쿼리를 직접 지정.
    //Java 텍스트 블록(""")이라 개행/들여쓰기 그대로 유지됨
    //의미 : 대상 행 : 해당 방(chat_room_id=:roomId) + 해당 사용자(user_no=:userNo).
    //      last_read_message_id를 조건부로 갱신 :
    //          NULL 이거나 현재 값 < :seen 이면 -> :seen 으로 업데이트
    //          그 외(이미 더 큰 값을 가리킴) -> 그대로 유지
    //      결과적으로 커서는 뒤로 가지 않음(단조 증가).
    //nativeQuery = true 라서 DB 방언 그대로 실행됨.
    @Query(
            value = """
        UPDATE chat_room_members
        SET last_read_message_id = CASE 
        WHEN last_read_message_id IS NULL OR last_read_message_id < :seen
        THEN :seen ELSE last_read_message_id END 
        WHERE chat_room_id=:roomId AND user_no=:userNo
""", nativeQuery = true
    )
    //메서드 시그니처.
    //@Param으로 쿼리의 바인딩 변수(:roomId, :userNo, :seen)에 자바 인자를 매핑.
    //반환값 int는 업데이트된 행 수(보통 0 또는 1).
    //      1 -> 해당 멤버 행 존재했고, 조건 총족하여 값이 바뀌었거나 동일로 처리됨
    //      0 -> 해당 멤버 행이 없거나(가입 안 됨), 조건상 업데이트가 일어나지 않음(이미 더 앞 id)
    int advanceCursor(@Param("roomId") Integer  roomId, @Param("userNo") Integer userNo, @Param("seen") Integer seen);
}
