package com.hagglemarket.marketweb.bid.controller;

import com.hagglemarket.marketweb.bid.dto.BidRequestDTO;
import com.hagglemarket.marketweb.bid.dto.BidResponseDTO;
import com.hagglemarket.marketweb.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j //log.info 사용가능 어노테이션
@Controller //컨트롤러 명시
@RequiredArgsConstructor //final 필드 자동 생성자
public class BidStompController {   //STOMP 메시지 처리 담당 컨트롤러.

    private final BidService bidService;  //비즈니스 로직 처리 / 입찰 금액 검증, 동시성 제어, DB 저장 등을 담당.
    private final SimpMessagingTemplate messagingTemplate; //WebSocket STOMP 메시지를 특정 구독자들에게 전송하는 스프링 제공 유틸 클래스

    /**
     * 클라이언트 -> 서버 : /app/bid.place     <-로 메시지를 보내면 이 컨트롤러가 실행됨.
     * 서버 -> 구독자 : /topic/auction.{auctionId} <-를 구독 중인 모든 사용자에게 브로드캐스트.
     */

    @MessageMapping("/bid.place")       // PostMapping 처럼 동작. /app/bid.place 경로로 들어온 WebSocket 메시지를 처리.
    public void placeBid(BidRequestDTO req){ //BidRequestDTO는 클라이언트에서 보낸 JSON 데이터를 매핑한 DTO. 예: { "auctionId": 1, "userNo": 5, "bidAmount": 20000 }
        //서비스 로직 수행(검정/동시성/DB 저장)
        BidResponseDTO res = bidService.placeBid(req); //placeBid 메서드에서 입찰 가능 여부 확인, 동시성 제어, DB 저장 후 결과는 BidResponseDTO로 반환 -> 성공 여부, 현재 최고가, 입찰자 정보 등이 담김.

        //토픽 문자열 / 결과를 경매방 구독자 전체에게 브로드캐스트 / 경매방의 모든 구독자에게 실시간 전송
        String topic = "/topic/auction." + req.getAuctionId(); //"/topic/auction.{auctionId}"는 방 별 채널

        //성공 시에만 브로드캐스트
        if(res.isSuccess()) {
            messagingTemplate.convertAndSend(topic, res);  //convertAndSend()는 지정된 경로를 구독 중인 모든 클라이언트에게 res를 전송.
            //로그로 확인
            log.info("placeBid: auctionId={}, userNo={}, bidAmount={}, success={}, topic={}",
                    req.getAuctionId(), req.getUserNo(), req.getBidAmount(), res.isSuccess(), topic);
        }else {
            // 실패는 브로드캐스트 안 함 (나중에 개인 큐로 대체)
            log.warn("placeBid: auctionId={}, userNo={}, bidAmount={}, success={}, topic={}",
                    req.getAuctionId(), req.getUserNo(), req.getBidAmount(), res.isSuccess(), topic);
        }

        // ⚠️ 개인 큐(/user/queue/...) 전송은 인터셉터로 Principal 세팅 후 추가 예정
    }

}
