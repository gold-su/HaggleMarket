package com.hagglemarket.marketweb.chat.service;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;

//이 서비스는 채팅 메시지 전송(저장 + 브로드캐스트 트리거)과 메시지 페이지 조회(최신/이전 더보기)를 담당하는 도메인 서비스 인터페이스입니다.
//sendChat(...)는 한 방(roomId)에 특정 사용자( senderNo)가 내용(content)을 보낸 기록을 만들고 반환한다.
//clientMsgId로 중복 전송 방지(멱등성)를 지원할 수 있다.
//getMessage(...)는 방의 메시지를 페이지로 가져오기 위해, beforeId 기준의 키셋 페이지네이션(무한스크롤 "이전 더보기")을 제공한다.
//채팅 메시지 관련 기능을 노출하는 서비스 인터페이스.
//구현체에서 실제 로직을 만든다.
public interface ChatMessageService {

    //메시지 전송 API의 서비스 시그니처.
    //roomId :  메시지를 보낼 채팅방 식별자.
    //senderNo : 보낸 사람의 유저 번호(PK). (권한/멤버십 검증 포인트)
    //content : 본문 텍스트. (길이/금칙어/공백만 입력 등 검증 포인트)
    //clientMsgId : 클라이언트가 생성한 임시 식별자(nullable 가능). 멱등성키로 활용
    //반환값 : 저장된 ChatMessage 엔티티(보통 DB 에서 생성된 id, createdAt 포함)
    ChatMessage sendChat(int roomId, int senderNo, String content, Long clientMsgId);

    //메시지 조회(페이지) API
    //roomId: 조회할 방.
    //beforeId: 이 값보다 작은 id 들만 가져오자는 기준(무한 스크롤 "이전 더보기"). null이면 최신부터.
    //size : 한 번에 가져올 개수(페이지 크기.)
    //반환값 : Page<ChatMessage> -> 콘텐츠 리스트 + hasNext/totalElements 등 메타 포함.
    //구현에서는 보통 findByRoom_IdOrderByIdDesc(최초) + findByRoom_IdAndIdLessThanOrderByIdDesc(이전) 패턴을 사용.
    Page<ChatMessage> getMessages(int roomId, Integer beforeId, int size);
}
