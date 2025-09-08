package com.hagglemarket.marketweb.chat.domain.entity;

import com.hagglemarket.marketweb.chat.enums.RoomKind;
import com.hagglemarket.marketweb.chat.enums.RoomStatus;
import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name = "chat_rooms")
@Getter
@Setter
public class ChatRoom {

    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name="chat_room_id")
    private Integer id;

    @Enumerated(EnumType.STRING)  //enum을 문자열로 저장
    @Column(name="room_kind", nullable = false)
    private RoomKind roomKind;

    //이 방이 어떤 대상에 묶인 방인지 나타냄.
    //roomKind=POST ➜ postId 사용
    //roomKind=AUCTION ➜ auctionId 사용
    //roomKind=ORDER ➜ orderId 사용
    @Column(name="post_id")
    private Integer postId;
    @Column(name="auction_id")
    private Integer auctionId;
    @Column(name="order_id")
    private Integer orderId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_user_no", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_user_no", nullable = false)
    private User buyer;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private RoomStatus roomStatus;

    @Column(name="created_at", updatable = false) //이후 수정되지 않도록 updatable=false
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false, updatable = false) //insert, update에 포함시키지 않음
    private LocalDateTime updatedAt;

}
