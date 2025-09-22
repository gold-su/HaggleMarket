package com.hagglemarket.marketweb.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendMessageReq {
    //senderNo는 보통 JWT 에서 뽑고, 여기선 빼는 게 안점함 (위조 방지)
    private String content;
    private Long clientMsgId; //멱등키
}
