package com.hagglemarket.marketweb.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
//클라이언트가 "메시지 전송" 요청 시 보내는 바디.
//senderNo는 JWT 에서 서버가 추출하고, 여기 DTO 에는 두지 않는 설계
public class SendMessageReq {
    //senderNo는 보통 JWT 에서 뽑고, 여기선 빼는 게 안전함 (위조 방지)
    private String content; //메시지 본문
    private Long clientMsgId; //클라이언트가 생성한 임시 id(멱등 처리용). 네트워크 재시도 시 중복 저장 방지.
}
