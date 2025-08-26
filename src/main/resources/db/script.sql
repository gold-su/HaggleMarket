/*
DROP  DATABASE hagglemarket;
create database hagglemarket;
use hagglemarket;
*/

#일단 user 테이블만 생성 / 다른 기능 구현할 때 차차 생성
create table category
(
    id        int auto_increment
        primary key,
    name      varchar(50) not null,
    parent_id int         null,
    constraint category_ibfk_1
        foreign key (parent_id) references category (id)
);

create index parent_id
    on category (parent_id);

create table users
(
    user_no      int auto_increment
        primary key,
    user_id      varchar(20)  not null,
    user_name    varchar(10)  not null,
    password     varchar(255) not null,
    phone_number varchar(11)  not null,
    nick_name    varchar(15)  not null,
    address      varchar(30)  not null,
    email        varchar(50)  not null,
    image_url    text         null,
    created_at   datetime     null,
    status       varchar(20)  null,
    rating       decimal      null,
    road_rating  decimal      null,
    constraint `unique`
        unique (user_id, email, phone_number)
);

create table auction_posts
(
    auction_id     int auto_increment
        primary key,
    user_no        int                                                                       not null,
    category_id    int                                                                       null,
    title          varchar(50)                                                               not null,
    content        text                                                                      not null,
    start_cost     int                                                                       not null,
    current_cost   int                                                                       not null,
    buyout_cost    int                                                                       null,
    start_time     datetime                                                                  not null,
    end_time       datetime                                                                  not null,
    winner_user_no int                                                                       null,
    hit            int                                             default 0                 not null,
    bid_count      int                                             default 0                 not null,
    created_at     datetime                                        default CURRENT_TIMESTAMP null,
    updated_at     datetime                                        default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    status         enum ('READY', 'ONGOING', 'ENDED', 'CANCELLED') default 'READY'           null,
    like_count     int                                             default 0                 not null,
    constraint fk_auction_posts_user
        foreign key (user_no) references users (user_no),
    constraint fk_auction_posts_winner
        foreign key (winner_user_no) references users (user_no)
);

create table auction_post_images
(
    image_id   int auto_increment
        primary key,
    auction_id int                                not null,
    image_data mediumblob                         not null,
    image_name varchar(255)                       not null,
    image_type varchar(50)                        not null,
    sort_order int                                not null,
    created_at datetime default CURRENT_TIMESTAMP null,
    constraint fk_auction_post_images
        foreign key (auction_id) references auction_posts (auction_id)
            on delete cascade
);

create table bids
(
    bid_id         int auto_increment
        primary key,
    auction_id     int                                not null,
    bidder_user_no int                                not null,
    bid_amount     int                                not null,
    bid_time       datetime default CURRENT_TIMESTAMP null,
    constraint fk_bids_auction_posts
        foreign key (auction_id) references auction_posts (auction_id)
            on delete cascade,
    constraint fk_bids_user
        foreign key (bidder_user_no) references users (user_no)
            on delete cascade
);

create table posts
(
    post_id        int auto_increment
        primary key,
    user_no        int                                not null,
    title          varchar(50)                        not null,
    cost           int                                not null,
    content        text                               not null,
    hit            int      default 0                 not null,
    created_at     datetime default (now())           null,
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    status         varchar(20)                        null,
    delivery_fee   tinyint(1)                         not null,
    swapping       tinyint(1)                         not null,
    category_id    int                                null,
    negotiable     tinyint(1)                         not null,
    product_status varchar(20)                        not null,
    constraint posts_users_user_no_fk
        foreign key (user_no) references users (user_no)
);

create table post_images
(
    image_no   int auto_increment
        primary key,
    post_id    int          not null,
    image_url  varchar(255) not null,
    sort_order int          not null,
    constraint post_images_posts_post_id_fk
        foreign key (post_id) references posts (post_id)
);

create table post_like
(
    id         int auto_increment
        primary key,
    user_no    int                                not null,
    post_id    int                                not null,
    created_at datetime default CURRENT_TIMESTAMP null,
    constraint uq_user_post
        unique (user_no, post_id),
    constraint fk_like_post
        foreign key (post_id) references posts (post_id),
    constraint fk_like_user
        foreign key (user_no) references users (user_no)
);

create index idx_post
    on post_like (post_id);

create index idx_user
    on post_like (user_no);

create table withdraw_users
(
    no          int auto_increment
        primary key,
    user_id     varchar(20)                        not null,
    user_email  varchar(50)                        null,
    withdraw_at datetime default CURRENT_TIMESTAMP not null
);

-- 채팅방: 상품문의(POST), 경매문의(AUCTION), 주문 채팅(ORDER)까지 한 테이블로
CREATE TABLE chat_rooms (
                            chat_room_id     INT AUTO_INCREMENT PRIMARY KEY,            -- 각 방의 고유 식별자
                            room_kind        ENUM('POST','AUCTION','ORDER') NOT NULL,   -- 방 유형 / POST : 일반 상품 문의 방, AUCTION : 경매글 문의/거래 방, ORDER : 주문/거래 진행 방
                            post_id          INT NULL,                                  -- room_kind = POST 일 때 사용
                            auction_id       INT NULL,                                  -- room_kind = AUCTION 일 때 사용
                            order_id         INT NULL,                                  -- room_kind = ORDER (나중 확장) 일 때 사용
                            seller_user_no   INT NOT NULL,                              -- 방의 판매자 유저 번호
                            buyer_user_no    INT NOT NULL,                              -- 방의 구매자 유저 번호
                            status           ENUM('ACTIVE','CLOSED') NOT NULL DEFAULT 'ACTIVE',     -- 채팅방 상태. 기본값은 활성(ACTIVE), 종료 시 CLOSED.
                            created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,           -- 방 생성 시각
                            updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,   -- 레코드가 수정될 때마다 자동으로 갱신되는 타임스탬프.

                            -- 유니크 정책: 동일 리소스/판매자/구매자 조합에 방 1개씩
                            UNIQUE KEY uq_room_post    (room_kind, post_id,    seller_user_no, buyer_user_no),     -- room_kind='POST' + 동일 post_id + 동일 판매자/구매자 조합의 방은 1개만 허용
                            UNIQUE KEY uq_room_auction (room_kind, auction_id, seller_user_no, buyer_user_no),     -- 위랑 같은 설명. auction일 때
                            UNIQUE KEY uq_room_order   (room_kind, order_id,   seller_user_no, buyer_user_no),     -- 위랑 같은 설명. order일 때

                            -- 탐색/필터 인덱스
                            INDEX idx_room_seller (seller_user_no),      -- 내가 판매자인 방 리스트 빠르게 조회.
                            INDEX idx_room_buyer  (buyer_user_no),       -- 내가 구매자인 방 리스트 빠르게 조회.

                            -- FK (상품/경매 삭제 시 방을 함께 지울지, 남길지 정책 선택)
                            -- 상품(게시글) 삭제 시, 관련 방의 post_id,auction_id를 NULL로 설정해서 방은 유지.
                            -- 과거 대화 기록 보존을 위한 선택.
                            CONSTRAINT fk_room_post
                                FOREIGN KEY (post_id) REFERENCES posts(post_id)
                                    ON DELETE SET NULL,
                            CONSTRAINT fk_room_auction
                                FOREIGN KEY (auction_id) REFERENCES auction_posts(auction_id)
                                    ON DELETE SET NULL,
                            -- 판매자, 구매자 FK
                            CONSTRAINT fk_room_seller
                                FOREIGN KEY (seller_user_no) REFERENCES users(user_no),
                            CONSTRAINT fk_room_buyer
                                FOREIGN KEY (buyer_user_no) REFERENCES users(user_no),

                            -- 선택: 판매자/구매자 동일 금지 (MySQL 8+에서 CHECK 동작)
                            -- 같은 사람이 판매자/구매자 중복으로 들어오지 못하게 방지.
                            CONSTRAINT chk_distinct_users CHECK (seller_user_no <> buyer_user_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ENGINE=InnoDB : 트랜잭션(ACID), 외래키(FK), 행 단위 잠금(row-level locking), 충돌 복구, MVCC 지원. 채팅/거래처럼 동시성이 높은 서비스엔 사실상 표준 선택입니다.
-- ENGINE=InnoDB는 단순히 “테이블을 저장하는 방식(스토리지 엔진)”을 MySQL 에서 InnoDB로 지정한다는 뜻
-- 트랜잭션, 외래키, 행 단위 잠금(row-level locking), 충돌 복구(crash recovery) 같은 기능을 쓸 수 있게 해주는 게 InnoDB의 핵심 장점
-- DEFAULT CHARSET=utf8mb4 : 진짜 UTF-8(4바이트): 한글, 이모지(😀), 각종 기호까지 안전하게 저장.




밑은 예전꺼
---------------------------------------------------------------------

create table chattinggroup (

                               chattingRoomId int auto_increment primary key,
                               postId         int         not null,
                               sellerId       int         not null,
                               buyerId        int         not null,
                               created        datetime    null,
                               updated        datetime    null,
                               status         varchar(10) null,
                               constraint chattingGroup_post_postId_fk
                                   foreign key (postId) references post (postId),
                               constraint chattingGroup_user_userNo_fk
                                   foreign key (sellerId) references user (userNo),
                               constraint chattingGroup_user_userNo_fk_2
                                   foreign key (buyerId) references user (userNo)
);


create table chattingcontent
(
    chattingRoomId    int         not null,
    chattingContentId int auto_increment
        primary key,
    userNo            int         not null,
    created           datetime    null,
    updated           datetime    null,
    status            varchar(10) null,
    constraint chattingContent_chattingGroup_chattingRoomId_fk
        foreign key (chattingRoomId) references chattinggroup (chattingRoomId),
    constraint chattingContent_user_userId_fk
        foreign key (userNo) references user (userNo)
);



create table searchhistory
(
    searchId   int auto_increment
        primary key,
    userNo     int         not null,
    searchText varchar(60) not null,
    constraint searchHistory_user_userId_fk
        foreign key (userNo) references user (userNo)
);

create table wishlist
(
    wishListId int auto_increment
        primary key,
    userNo     int      not null,
    postId     int      not null,
    created    datetime null,
    updated    datetime null,
    constraint wishList_post_userId_fk_2
        foreign key (postId) references post (postId),
    constraint wishList_user_userId_fk
        foreign key (userNo) references user (userNo)
);


