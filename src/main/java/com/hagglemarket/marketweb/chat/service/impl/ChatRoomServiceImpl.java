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

    //    사용자가 게시글 상세에서 채팅하기 클릭
    //    컨트롤러 → findOrCreate(POST, postId, sellerNo, buyerNo) 호출
    //    이미 있나? → 있으면 그 방 id 반환 / 없으면 새 방 저장
    //    멤버 2명 보장
    //    프론트는 반환된 roomId로 메시지 목록/입장 수행
    //    채팅 목록에서 내 방들 보기
    //    → listMyRooms(me, pageable)로 최신순 가져오기
    //    방에 들어가려고 할 때
    //    → getMyRoom(roomId, me)로 권한 체크 포함하여 조회
    //    거래 끝나서 방 닫기
    //    → closeRoom(roomId, me)로 상태 CLOSED
    //    다시 대화 필요
    //    → reopenRoom(roomId, me)로 상태 ACTIVE

    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Override @Transactional //이 메서드 안의 DB 작업(조회 -> 저장 -> 후처리)을 하나의 트랜잭션으로 묶어 보장.
    public ChatRoom findOrCreate(RoomKind kind, Integer resourceId, Integer sellerUserNo, Integer buyerUserNo){
        //필수 인자가 빠지면 400
        if(sellerUserNo == null || buyerUserNo == null) throw new ResponseStatusException(BAD_REQUEST,"seller/buyer required");
        //판매자 = 구매자면 비즈니스 정책 위반으로 400
        if(sellerUserNo.equals(buyerUserNo)) throw new ResponseStatusException(BAD_REQUEST,"seller == buyer not allowed");
        //어떤 리소스(게시글/경매/주문)인지 식별하는 resourceId 필수.
        if(resourceId == null) throw new ResponseStatusException(BAD_REQUEST,"resource id required");

        // 1) 존재하면 재사용 (유니크 정책과 동일 조회)
        // 중복 방 방지 : 동일한 (종류, 대상 ID, seller. buyer) 조합은 하나의 방만 유지하려는 설계.
        // 성공 시 바로 반환 -> 불필요한 insert 방지.
        // 이게 잘 작동하려면 DB에 유니크 인덱스(종류별로 부분 유니크)가 있어야 경쟁 상태에서도 안전.
        var existing  = switch (kind){
            case POST -> roomRepo.findByRoomKindAndPostIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
            case AUCTION -> roomRepo.findByRoomKindAndAuctionIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
            case ORDER -> roomRepo.findByRoomKindAndOrderIdAndSeller_UserNoAndBuyer_UserNo(kind, resourceId, sellerUserNo, buyerUserNo);
        };
        if(existing.isPresent()) return existing.get();

        // 2) 리소스/판매자 검증 (가능하면)


        // 3) 유저 로드
        // 존재하지 않는 유저면 404
        User seller = userRepo.findById(sellerUserNo).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "seller not found"));
        // 이후 엔티티 관계 세팅에 사용
        User buyer = userRepo.findById(buyerUserNo).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "buyer not found"));

        // 4) 생성 (동시성: 유니크 키 충돌 처리)
        // roomKind 별로 연결되는 대상 id를 정확히 1개만 세팅.
        // 판매자/구매자 연관관계 세팅.
        // ACTIVE 상태로 시작.
        // "멤버 레코드"를 둘 다 보장(없으면 생성).
        // 읽음 커서/권한 체크/참여자 조회 등에 쓰임.
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
            //동시에 두 요청이 들어와 둘 다 생성하려 할 때 -> DB 유니크 제약 위반 발생
            //예외 캐치 후 이미 만들어진 것을 재조회해 반환 -> 멱등성/경쟁 상태 안전
            //전체 : DB 레벨에 유니크 인덱스가 있어야 이 패턴이 완성됨 (종류별로 room_kind, post_id, seller_user_no, buyer_user_no 등)
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
        //방이 없으면 404.
        var room = roomRepo.findById(roomId).orElseThrow(()->new ResponseStatusException(NOT_FOUND, "room not found"));

        //내가 판매자도 아니고, 구매자도 아니면 -> 403
        if(room.getSeller().getUserNo() != meUserNo && room.getBuyer().getUserNo() != meUserNo){
            throw new ResponseStatusException(FORBIDDEN, "not a member of the room");
        }
        return room;
    }

    @Override
    public Page<ChatRoom> listMyRooms(Integer meUserNo, Pageable page){
        //판매자 또는 구매자로 내가 속한 방들
        //updated_at DESC 정렬 -> "최근 대화 순" 목록에 유용
        //성능 포인트 : chat_rooms.updated_at에 인덱스 있으면 매우 빠름
        return roomRepo.findBySeller_UserNoOrBuyer_UserNoOrderByUpdatedAtDesc(meUserNo, meUserNo, page);
    }

    @Override
    @Transactional //"이 메서드를 DB 작업을 하나의 트랜잭션으로 묶어줘!"라는 애너테이션
    //채팅방 닫기
    public void closeRoom(Integer roomId, Integer meUserNo){
        var room = getMyRoom(roomId, meUserNo);
        room.setStatus(RoomStatus.CLOSED);
    }

    @Override
    @Transactional //"이 메서드를 DB 작업을 하나의 트랜잭션으로 묶어줘!"라는 애너테이션
    //채팅방 재오픈
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
