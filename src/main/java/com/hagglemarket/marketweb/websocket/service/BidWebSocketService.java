package com.hagglemarket.marketweb.websocket.service;

import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service //서비스 명시
@RequiredArgsConstructor    //final 필드 자동 생성자
public class BidWebSocketService {

    //실제로 메시지를 경로에 전송하는 역할을 하는 스프링 객체.
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 경매방 브로드캐스트 전송
     * @Param auctionId 경매글 ID
     * @Param response 입찰 처리 후 브로드캐스트 할 응답 데이터
     */

    //broadcastBid 메서드: 특정 경매방의 모든 구독자에게 입찰 결과를 보내는 기능.
    public void broadcastBid(int auctionId, BidResponseDTO response){
        //destination → /topic/auction.{auctionId} 형태로 경매방 구독 경로 지정
        String destination = "/topic/auction."+auctionId;
        //convertAndSend() → 해당 경로를 구독 중인 모든 클라이언트에게 response 전송.
        simpMessagingTemplate.convertAndSend(destination, response);
    }
}
