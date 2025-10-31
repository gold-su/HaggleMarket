package com.hagglemarket.marketweb.chat.controller;

import com.hagglemarket.marketweb.auction.service.AuctionService;
import com.hagglemarket.marketweb.chat.dto.ChatRoomRes;
import com.hagglemarket.marketweb.chat.dto.CreateRoomReq;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import com.hagglemarket.marketweb.chat.repository.ChatMessageRepository;
import com.hagglemarket.marketweb.chat.service.ChatRoomService;
import com.hagglemarket.marketweb.post.service.PostService;
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
    private final PostService postService;
    private final AuctionService auctionService;
    private final ChatMessageRepository chatMessageRepository;

    //방 생성(또는 기존 방 재사용): 멱등
    @PostMapping(value = "/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatRoomRes createRes(@AuthenticationPrincipal CustomUserDetails me,
                                 @RequestBody @Valid  CreateRoomReq req) {
        //자원 ID 추출
        Integer resourceId = switch (req.getRoomKind()){
            case POST -> req.getPostId();
            case AUCTION -> req.getAuctionId();
            case ORDER -> req.getOrderId();
            case BOT -> null;
        };
        // BOT은 resourceId 필요 없음
        if (req.getRoomKind() != RoomKind.BOT && resourceId == null) {
            throw new IllegalArgumentException("resource id is required for " + req.getRoomKind());
        }
        //판매자 userNo 조합
        //판매자 userNo를 서버 내부에서 조회
        Integer sellerUserNo = switch (req.getRoomKind()){
            case POST -> postService.getSellerUserNoByPostId(resourceId);
            case AUCTION -> auctionService.getSellerUserNoByAuctionId(resourceId);
            case ORDER -> me.getUserNo(); // AI 방이라면 자기 자신 기준으로 생성
            case BOT -> null;
        };

        //방 생성 or 기존 방 재사용
        var room = roomService.findOrCreate(req.getRoomKind(), resourceId, sellerUserNo, me.getUserNo());

        //단일 인자 버전 호출
        return ChatRoomRes.from(room);
    }

    //내 방 목록
    @GetMapping("/rooms")
    public Page<ChatRoomRes> getMyRooms(@AuthenticationPrincipal CustomUserDetails me,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size
                                        ){
        var rooms = roomService.listMyRooms(me.getUserNo(), PageRequest.of(page, size));

        return  rooms.map(room->{
            //가장 최근 메시지 1개 가져오기
            var latest = chatMessageRepository.findTop1ByRoom_IdOrderByIdDesc(room.getId()).orElse(null);
            return ChatRoomRes.from(room, me.getUserNo(), latest);
        });
    }

    //방 상세(접근권한 확인)
    @GetMapping("/rooms/{roomId}")
    public ChatRoomRes getRoom(@AuthenticationPrincipal CustomUserDetails me,
                               @PathVariable Integer roomId
                               ){
        var room = roomService.getMyRoom(roomId, me.getUserNo());
        return ChatRoomRes.from(room, me.getUserNo(), null);
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
