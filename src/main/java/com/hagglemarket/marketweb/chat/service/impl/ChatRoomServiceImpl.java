package com.hagglemarket.marketweb.chat.service.impl;

import com.hagglemarket.marketweb.chat.domain.entity.ChatRoom;
import com.hagglemarket.marketweb.chat.domain.entity.ChatRoomMember;
import com.hagglemarket.marketweb.chat.domain.id.ChatRoomMemberId;
import com.hagglemarket.marketweb.chat.enums.RoomKind;
import com.hagglemarket.marketweb.chat.enums.RoomStatus;
import com.hagglemarket.marketweb.chat.repository.ChatRoomMemberRepository;
import com.hagglemarket.marketweb.chat.repository.ChatRoomRepository;
import com.hagglemarket.marketweb.chat.service.ChatRoomService;
import com.hagglemarket.marketweb.user.entity.User;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Override @Transactional
    public ChatRoom findOrCreate(RoomKind kind, Integer resourceId, Integer sellerUserNo, Integer buyerUserNo){
        if(sellerUserNo == null || buyerUserNo == null) throw new ResponseStatusException(BAD_REQUEST,"seller/buyer required");
        if(sellerUserNo.equals(buyerUserNo)) throw new ResponseStatusException(BAD_REQUEST,"seller == buyer not allowed");
        if(resourceId == null) throw new ResponseStatusException(BAD_REQUEST,"resource id required");

        // 1) 존재하면 재사용 (유니크 정책과 동일 조회)
        var existing  = switch (kind){
            case POST -> roomRepo.findByRoomKindAndPostIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
            case AUCTION -> roomRepo.findByRoomKindAndAuctionIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
            case ORDER -> roomRepo.findByRoomKindAndOrderIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
        };
        if(existing.isPresent()) return existing.get();

        // 2) 리소스/판매자 검증 (가능하면)


        // 3) 유저 로드
        User seller = userRepo.findById(sellerUserNo).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "seller not found"));
        User buyer = userRepo.findById(buyerUserNo).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "buyer not found"));

        // 4) 생성 (동시성: 유니크 키 충돌 처리)
        try{
            var room = new ChatRoom();
            room.setRoomKind(kind);
            switch (kind){
                case POST -> room.setPostId(resourceId);
                case AUCTION -> room.setAuctionId(resourceId);
                case ORDER -> room.setOrderId(resourceId);
            }
            room.setSeller(seller);
            room.setBuyer(buyer);
            room.setStatus(RoomStatus.ACTIVE);

            var saved = roomRepo.save(room);

            // 5) 멤버 커서 보장
            ensureMember(saved, seller);
            ensureMember(saved, buyer);

            return saved;
        } catch (DataIntegrityViolationException e){
            //경쟁 상태로 동시에 INSERT -> 유니크 키 위반 -> 이미 생긴 방 재조회
            return switch (kind){
                case POST -> roomRepo.findByRoomKindAndPostIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo)
                        .orElseThrow(() -> new ResponseStatusException(CONFLICT, "room exists but not retrievable"));
                case AUCTION -> roomRepo.findByRoomKindAndAuctionIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo)
                        .orElseThrow(() -> new ResponseStatusException(CONFLICT, "room exists but not retrievable"));
                case ORDER -> roomRepo.findByRoomKindAndOrderIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo)
                        .orElseThrow(() -> new ResponseStatusException(CONFLICT, "room exists but not retrievable"));
            };
        }
    }

    @Override
    public ChatRoom getMyRoom(Integer roomId, Integer meUserNo){
        var room = roomRepo.findById(roomId).orElseThrow(()->new ResponseStatusException(NOT_FOUND, "room not found"));

        //내가 판매자도 아니고, 구매자도 아니면 -> 403
        if(room.getSeller().getUserNo() != meUserNo && room.getBuyer().getUserNo() != meUserNo){
            throw new ResponseStatusException(FORBIDDEN, "not a member of the room");
        }
        return room;
    }

    @Override
    public Page<ChatRoom> listMyRooms(Integer meUserNo, Pageable page){
        return roomRepo.findBySeller_UserNoOrBuyer_UserNoOrderByUpdatedAtDesc(meUserNo, meUserNo, page);
    }

    @Override
    @Transactional
    public void closeRoom(Integer roomId, Integer meUserNo){
        var room = getMyRoom(roomId, meUserNo);
        room.setStatus(RoomStatus.CLOSED);
    }

    @Override
    @Transactional //"이 메서드를 DB 작업을 하나의 트랜잭션으로 묶어줘!"라는 애너테이션
    public void reopenRoom(Integer roomId, Integer meUserNo){
        var room = getMyRoom(roomId, meUserNo);
        room.setStatus(RoomStatus.ACTIVE);
    }

    private void ensureMember(ChatRoom room, User user){
        var id = new ChatRoomMemberId(room.getId(), user.getUserNo());
        memberRepo.findById(id).orElseGet(() -> {
            var m = new ChatRoomMember();
            m.setId(id);
            m.setRoom(room);
            m.setUser(user);
            return memberRepo.save(m);
        });
    }
}
