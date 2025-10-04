package com.hagglemarket.marketweb.chat.service;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    //주어진 조건에 맞는 채팅방이 있으면 반환, 없으면 새로 생성
    ChatRoom findOrCreate(RoomKind kind, Integer resourceId, Integer sellerUserNo, Integer buyerUserNo);
    //방 상세 조회 + 내가 참가자인지 검증, 특정 roomId로 입장할 때
    ChatRoom getMyRoom(Integer roomId, Integer meUserNo); //접근 권한 확인용
    //내가 참여 중인 모든 방 목록 조회, "내 채팅목록" 페이지
    Page<ChatRoom> listMyRooms(Integer meUserNo, Pageable pageable); //내 방 목록
    //해당 유저가 속한 방을 닫기(비활성화), 거래 완료/나가기
    void closeRoom(Integer roomId, Integer meUserNo);  //방 닫기(참여자만)
    //닫힌 방을 다시 열기, 필요 시 다시 채팅 재게
    void reopenRoom(Integer roomId, Integer meUserNo); //방 다시 열기(참여자만)
}
