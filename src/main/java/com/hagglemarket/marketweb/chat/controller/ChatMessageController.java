package com.hagglemarket.marketweb.chat.controller;

import com.hagglemarket.marketweb.chat.domain.entity.ChatMessage;
import com.hagglemarket.marketweb.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor //final 필드 생성자 자동
public class ChatMessageController {

    private final ChatMessageService service;


    //메시지 전송
    @PostMapping("/rooms/{roomId}/messages")
    public ChatMessage send(@PathVariable int roomId,
                            @RequestParam int senderNo,
                            @RequestParam String content,
                            @RequestParam(required = false) Long clientMsgId
    ) {
       return service.sendChat(roomId, senderNo, content, clientMsgId);
    }

    //메시지 히스토리 (최신 N개, 이전 더보기 지원)
    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessage> history(@PathVariable int roomId,
                                     @RequestParam(required = false) Integer beforeId,
                                     @RequestParam(defaultValue = "30") int size
                                     ){
        return service.getMessage(roomId, beforeId, size);
    }
}
