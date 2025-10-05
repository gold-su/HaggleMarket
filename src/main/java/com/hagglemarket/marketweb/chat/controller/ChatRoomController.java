package com.hagglemarket.marketweb.chat.controller;

import com.hagglemarket.marketweb.chat.dto.ChatRoomRes;
import com.hagglemarket.marketweb.chat.dto.CreateRoomReq;
import com.hagglemarket.marketweb.chat.service.ChatRoomService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService roomService;

    //방 생성(또는 기존 방 재사용): 멱등
    @PostMapping(value = "/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatRoomRes createRes(@AuthenticationPrincipal CustomUserDetails me,
                                 @RequestBody@Valid CreateRoomReq req) {
        Integer resourceId = switch (req.getRoomKind()){
            case POST -> req.getPostId();
            case AUCTION -> req.getAuctionId();
            case ORDER -> req.getOrderId();
        };
        if(resourceId == null) throw new IllegalArgumentException("resource id is required for " + req.getRoomKind());

        var room = roomService.findOrCreate(req.getRoomKind(), resourceId, req.getSellerUserNo(), me.getUserNo());
        return ChatRoomRes.from(room);
    }

    //내 방 목록
    @GetMapping("/rooms")
    public Page<ChatRoomRes> getMyRooms(@AuthenticationPrincipal CustomUserDetails me,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size
                                        ){
        return  roomService.listMyRooms(me.getUserNo(), PageRequest.of(page, size)).map(ChatRoomRes::from);
    }

    //방 상세(접근권한 확인)
    @GetMapping("/rooms/{roomId}")
    public ChatRoomRes getRoom(@AuthenticationPrincipal CustomUserDetails me,
                               @PathVariable Integer roomId
                               ){
        return ChatRoomRes.from(roomService.getMyRoom(roomId, me.getUserNo()));
    }

    //방 닫기
    @PatchMapping("/rooms/{roomId}/close")
    public void colse(@AuthenticationPrincipal CustomUserDetails me,
                      @PathVariable Integer roomId
                      ){
        roomService.closeRoom(roomId, me.getUserNo());
    }

    //방 다시 열기
    @PatchMapping("/rooms/{roomId}/reopen")
    public void reopen(@AuthenticationPrincipal CustomUserDetails me,
                       @PathVariable Integer roomId
                       ){
        roomService.reopenRoom(roomId, me.getUserNo());
    }
}
