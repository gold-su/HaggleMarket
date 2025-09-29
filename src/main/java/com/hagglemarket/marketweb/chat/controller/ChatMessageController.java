package com.hagglemarket.marketweb.chat.controller;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.dto.ChatMessageRes;
import com.hagglemarket.marketweb.chat.dto.SendMessageReq;
import com.hagglemarket.marketweb.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor //final 필드 생성자 자동
public class ChatMessageController {

    private final ChatMessageService service;


    //메시지 전송
    @PostMapping(value = "/rooms/{roomId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatMessageRes send(@PathVariable int roomId,
                            @AuthenticationPrincipal(expression = "userNo") Integer senderNo,
                            @RequestBody @Valid SendMessageReq req
                            ) {
        var saved = service.sendChat(roomId, senderNo, req.getContent(), req.getClientMsgId());
       return ChatMessageRes.from(saved);
    }

    //메시지 히스토리 (최신 N개, 이전 더보기 지원)
    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessageRes> history(@PathVariable int roomId,
                                     @RequestParam(required = false) Integer beforeId,
                                     @RequestParam(defaultValue = "30") int size
                                     ){
        return service.getMessages(roomId, beforeId, size).map(ChatMessageRes::from);
    }
}
