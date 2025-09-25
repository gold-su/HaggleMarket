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


use hagglemarket;

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
                            created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),     -- [변경] 방 생성 시각(밀리초 정밀도)
                            updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)      -- [변경] 레코드 수정 시각(밀리초 정밀도)
                                ON UPDATE CURRENT_TIMESTAMP(3),

    -- 유니크 정책: 동일 리소스/판매자/구매자 조합에 방 1개씩
                            UNIQUE KEY uq_room_post    (room_kind, post_id,    seller_user_no, buyer_user_no),     -- room_kind='POST' + 동일 post_id + 동일 판매자/구매자 조합의 방은 1개만 허용
                            UNIQUE KEY uq_room_auction (room_kind, auction_id, seller_user_no, buyer_user_no),     -- 위랑 같은 설명. auction일 때
                            UNIQUE KEY uq_room_order   (room_kind, order_id,   seller_user_no, buyer_user_no),     -- 위랑 같은 설명. order일 때

    -- 탐색/필터 인덱스
                            INDEX idx_room_seller (seller_user_no),                           -- 내가 판매자인 방 리스트 빠르게 조회.
                            INDEX idx_room_buyer  (buyer_user_no),                            -- 내가 구매자인 방 리스트 빠르게 조회.
                            INDEX idx_room_kind_post    (room_kind, post_id),                 -- 상품문의 방 찾기 최적화
                            INDEX idx_room_kind_auction (room_kind, auction_id),              -- 경매문의 방 찾기 최적화
                            INDEX idx_room_kind_order   (room_kind, order_id),                -- 주문 채팅 방 찾기 최적화

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

#    -- 1) 세 리소스 ID 중 "최대 하나만" NOT NULL
#   CONSTRAINT chk_at_most_one_resource
#     CHECK (
#       ((post_id IS NOT NULL) + (auction_id IS NOT NULL) + (order_id IS NOT NULL)) <= 1
#     ),
#   -- 2) 값이 있을 때만 유형 매칭을 강제 (값이 NULL이면 통과)
#   CONSTRAINT chk_post_kind_matches
#     CHECK (post_id    IS NULL OR room_kind='POST'),
#   CONSTRAINT chk_auction_kind_matches
#     CHECK (auction_id IS NULL OR room_kind='AUCTION'),
#   CONSTRAINT chk_order_kind_matches
#     CHECK (order_id   IS NULL OR room_kind='ORDER')

                            CONSTRAINT chk_distinct_users CHECK (seller_user_no <> buyer_user_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ENGINE=InnoDB : 트랜잭션(ACID), 외래키(FK), 행 단위 잠금(row-level locking), 충돌 복구, MVCC 지원. 채팅/거래처럼 동시성이 높은 서비스엔 사실상 표준 선택입니다.
-- ENGINE=InnoDB는 단순히 “테이블을 저장하는 방식(스토리지 엔진)”을 MySQL 에서 InnoDB로 지정한다는 뜻
-- 트랜잭션, 외래키, 행 단위 잠금(row-level locking), 충돌 복구(crash recovery) 같은 기능을 쓸 수 있게 해주는 게 InnoDB의 핵심 장점
-- DEFAULT CHARSET=utf8mb4 : 진짜 UTF-8(4바이트): 한글, 이모지(😀), 각종 기호까지 안전하게 저장.

-- 메시지 테이블 (본문 + SYSTEM 로그 + 빠른 페이지네이션)
CREATE TABLE chat_messages (
                               chat_message_id  INT AUTO_INCREMENT PRIMARY KEY,     -- 메시지 프라이머리 키
                               chat_room_id     INT NOT NULL,                       -- 어떤 채팅방의 메시지인지.
                               sender_user_no   INT NULL,                           -- SYSTEM 메시지일 땐 NULL / 발신자 FK
                               msg_type         ENUM('CHAT','SYSTEM') NOT NULL DEFAULT 'CHAT',  -- 일반 채팅 / 시스템 이벤트 구분 (예: 방 생성, 나가기, 판매자 변경, 가격 제안 수락 등)
                               content          TEXT,                               -- 메시지 본문 (SYSTEM일 땐 요약/이벤트 문구)
                               client_msg_id    BIGINT NULL,                        -- 클라 낙관 추가/중복 필터용(선택)
                               status           ENUM('NORMAL','DELETED') NOT NULL DEFAULT 'NORMAL', -- 소프트 삭제 상태. DELETED일 때 UI에서 “삭제된 메시지입니다” 처리.
                               created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),  -- [변경] 서버 기준 생성 시각(밀리초 정밀도)

                               CONSTRAINT fk_msg_room   FOREIGN KEY (chat_room_id)   REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE, -- 방이 삭제되면 그 방의 모든 메시지도 같이 삭제
                               CONSTRAINT fk_msg_sender FOREIGN KEY (sender_user_no) REFERENCES users(user_no),          -- 발신자가 실제 존재하는 유저인지 보장

    -- [추가] SYSTEM/CHAT 발신자 규칙 고정 (SYSTEM이면 sender_user_no IS NULL)
                               CONSTRAINT chk_sender_system
                                   CHECK ( (msg_type='SYSTEM' AND sender_user_no IS NULL) OR (msg_type='CHAT' AND sender_user_no IS NOT NULL) ),

    -- [추가] 클라 재전송 중복 방지 (NULL은 유니크 비교 제외)
                               UNIQUE KEY uq_client_dedup (chat_room_id, sender_user_no, client_msg_id),

    -- 히스토리 무한 스크롤/페이징 최적 인덱스
                               INDEX idx_room_created_pk (chat_room_id, created_at, chat_message_id), -- [변경] 시간 순 + PK로 안정 페이징
                               INDEX idx_msg_room_id      (chat_room_id, chat_message_id),            -- 방별 + 메시지 PK 범위/정렬에 최적화
                               INDEX idx_msg_room         (chat_room_id)                               -- 방별 단순 조회/count 최적화 (자주 쓰면 유지, 아니면 제거 가능)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 메시지 읽음 커서 테이블
CREATE TABLE chat_room_members (
                                   chat_room_id          INT NOT NULL,  -- 어떤 방에서 어떤 사용자의 읽음 상태인지 식별하는 복합 PK의 구성 요소
                                   user_no               INT NOT NULL,  -- 어떤 방에서 어떤 사용자의 읽음 상태인지 식별하는 복합 PK의 구성 요소
                                   last_read_message_id  INT NULL,      -- 이 사용자가 읽은 마지막 메시지 PK

                                   PRIMARY KEY (chat_room_id, user_no), -- 한 방에서 한 사용자는 행 1개만 갖도록 보장 (중복 방지)

                                   CONSTRAINT fk_member_room  FOREIGN KEY (chat_room_id)         REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE, -- 방이 삭제되면 해당 방의 멤버 커서도 함께 삭제
                                   CONSTRAINT fk_member_user  FOREIGN KEY (user_no)              REFERENCES users(user_no),                               -- 실제 존재하는 사용자만 기록
                                   CONSTRAINT fk_member_last  FOREIGN KEY (last_read_message_id) REFERENCES chat_messages(chat_message_id) ON DELETE SET NULL -- [변경] 메시지 삭제 시 커서를 NULL로 되돌림
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$

-- INSERT 시: 리소스는 최대 하나만, room_kind와 일치 강제
CREATE TRIGGER trg_chat_rooms_bi
    BEFORE INSERT ON chat_rooms
    FOR EACH ROW
BEGIN
    IF ((NEW.post_id IS NOT NULL) + (NEW.auction_id IS NOT NULL) + (NEW.order_id IS NOT NULL)) > 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only one of post_id/auction_id/order_id may be non-NULL';
    END IF;

    IF (NEW.post_id    IS NOT NULL AND NEW.room_kind <> 'POST')
        OR (NEW.auction_id IS NOT NULL AND NEW.room_kind <> 'AUCTION')
        OR (NEW.order_id   IS NOT NULL AND NEW.room_kind <> 'ORDER') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'room_kind must match the non-NULL resource column';
    END IF;
END$$

-- UPDATE 시: 동일 규칙
CREATE TRIGGER trg_chat_rooms_bu
    BEFORE UPDATE ON chat_rooms
    FOR EACH ROW
BEGIN
    IF ((NEW.post_id IS NOT NULL) + (NEW.auction_id IS NOT NULL) + (NEW.order_id IS NOT NULL)) > 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only one of post_id/auction_id/order_id may be non-NULL';
    END IF;

    IF (NEW.post_id    IS NOT NULL AND NEW.room_kind <> 'POST')
        OR (NEW.auction_id IS NOT NULL AND NEW.room_kind <> 'AUCTION')
        OR (NEW.order_id   IS NOT NULL AND NEW.room_kind <> 'ORDER') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'room_kind must match the non-NULL resource column';
    END IF;
END$$

DELIMITER ;

--------------------------------------------------------------------
현재 상태 sql 구문

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
    constraint uq_users_userid_email_phone
        unique (user_id, email, phone_number)
);

create table auction_posts
(
    auction_id     int auto_increment
        primary key,
    user_no        int                                                                         not null,
    category_id    int                                                                         null,
    title          varchar(50)                                                                 not null,
    content        text                                                                        not null,
    start_cost     int                                                                         not null,
    current_cost   int                                                                         not null,
    buyout_cost    int                                                                         null,
    start_time     datetime                                                                    not null,
    end_time       datetime                                                                    not null,
    winner_user_no int                                                                         null,
    hit            int                                             default 0                   not null,
    bid_count      int                                             default 0                   not null,
    created_at     datetime                                        default current_timestamp() null,
    updated_at     datetime                                        default current_timestamp() null on update current_timestamp(),
    status         enum ('READY', 'ONGOING', 'ENDED', 'CANCELLED') default 'READY'             null,
    like_count     int                                             default 0                   not null,
    constraint fk_auction_posts_user
        foreign key (user_no) references users (user_no),
    constraint fk_auction_posts_winner
        foreign key (winner_user_no) references users (user_no)
);

create table auction_post_images
(
    image_id   int auto_increment
        primary key,
    auction_id int                                  not null,
    image_data mediumblob                           not null,
    image_name varchar(255)                         not null,
    image_type varchar(50)                          not null,
    sort_order int                                  not null,
    created_at datetime default current_timestamp() null,
    constraint fk_auction_post_images
        foreign key (auction_id) references auction_posts (auction_id)
            on delete cascade
);

create table bids
(
    bid_id         int auto_increment
        primary key,
    auction_id     int                                  not null,
    bidder_user_no int                                  not null,
    bid_amount     int                                  not null,
    bid_time       datetime default current_timestamp() null,
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
    user_no        int                                    not null,
    title          varchar(50)                            not null,
    cost           int                                    not null,
    content        text                                   not null,
    hit            int        default 0                   not null,
    created_at     datetime   default current_timestamp() null,
    updated_at     datetime   default current_timestamp() null on update current_timestamp(),
    status         varchar(20)                            null,
    delivery_fee   tinyint(1) default 0                   not null,
    swapping       tinyint(1) default 0                   not null,
    category_id    int                                    null,
    negotiable     tinyint(1) default 0                   not null,
    product_status varchar(20)                            not null,
    like_count     int        default 0                   not null,
    constraint posts_users_user_no_fk
        foreign key (user_no) references users (user_no)
);

create table chat_rooms
(
    chat_room_id   int auto_increment
        primary key,
    room_kind      enum ('POST', 'AUCTION', 'ORDER')                      not null,
    post_id        int                                                    null,
    auction_id     int                                                    null,
    order_id       int                                                    null,
    seller_user_no int                                                    not null,
    buyer_user_no  int                                                    not null,
    status         enum ('ACTIVE', 'CLOSED') default 'ACTIVE'             not null,
    created_at     datetime(3)               default current_timestamp(3) not null,
    updated_at     datetime(3)               default current_timestamp(3) not null on update current_timestamp(3),
    constraint uq_room_auction
        unique (room_kind, auction_id, seller_user_no, buyer_user_no),
    constraint uq_room_order
        unique (room_kind, order_id, seller_user_no, buyer_user_no),
    constraint uq_room_post
        unique (room_kind, post_id, seller_user_no, buyer_user_no),
    constraint fk_room_auction
        foreign key (auction_id) references auction_posts (auction_id)
            on delete set null,
    constraint fk_room_buyer
        foreign key (buyer_user_no) references users (user_no),
    constraint fk_room_post
        foreign key (post_id) references posts (post_id)
            on delete set null,
    constraint fk_room_seller
        foreign key (seller_user_no) references users (user_no),
    constraint chk_distinct_users
        check (`seller_user_no` <> `buyer_user_no`)
);

create table chat_messages
(
    chat_message_id int auto_increment
        primary key,
    chat_room_id    int                                                     not null,
    sender_user_no  int                                                     null,
    msg_type        enum ('CHAT', 'SYSTEM')    default 'CHAT'               not null,
    content         text                                                    null,
    client_msg_id   bigint                                                  null,
    status          enum ('NORMAL', 'DELETED') default 'NORMAL'             not null,
    created_at      datetime(3)                default current_timestamp(3) not null,
    constraint uq_client_dedup
        unique (chat_room_id, sender_user_no, client_msg_id),
    constraint fk_msg_room
        foreign key (chat_room_id) references chat_rooms (chat_room_id)
            on delete cascade,
    constraint fk_msg_sender
        foreign key (sender_user_no) references users (user_no),
    constraint chk_sender_system
        check (`msg_type` = 'SYSTEM' and `sender_user_no` is null or
               `msg_type` = 'CHAT' and `sender_user_no` is not null)
);

create index idx_msg_room
    on chat_messages (chat_room_id);

create index idx_msg_room_id
    on chat_messages (chat_room_id, chat_message_id);

create index idx_room_created_pk
    on chat_messages (chat_room_id, created_at, chat_message_id);

create table chat_room_members
(
    chat_room_id         int not null,
    user_no              int not null,
    last_read_message_id int null,
    primary key (chat_room_id, user_no),
    constraint fk_member_last
        foreign key (last_read_message_id) references chat_messages (chat_message_id)
            on delete set null,
    constraint fk_member_room
        foreign key (chat_room_id) references chat_rooms (chat_room_id)
            on delete cascade,
    constraint fk_member_user
        foreign key (user_no) references users (user_no)
);

create index idx_room_buyer
    on chat_rooms (buyer_user_no);

create index idx_room_kind_auction
    on chat_rooms (room_kind, auction_id);

create index idx_room_kind_order
    on chat_rooms (room_kind, order_id);

create index idx_room_kind_post
    on chat_rooms (room_kind, post_id);

create index idx_room_seller
    on chat_rooms (seller_user_no);

create definer = root@localhost trigger trg_chat_rooms_bi
    before insert
    on chat_rooms
    for each row
BEGIN
    IF ((NEW.post_id IS NOT NULL) + (NEW.auction_id IS NOT NULL) + (NEW.order_id IS NOT NULL)) > 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only one of post_id/auction_id/order_id may be non-NULL';
    END IF;

    IF (NEW.post_id    IS NOT NULL AND NEW.room_kind <> 'POST')
        OR (NEW.auction_id IS NOT NULL AND NEW.room_kind <> 'AUCTION')
        OR (NEW.order_id   IS NOT NULL AND NEW.room_kind <> 'ORDER') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'room_kind must match the non-NULL resource column';
    END IF;
END;

create definer = root@localhost trigger trg_chat_rooms_bu
    before update
    on chat_rooms
    for each row
BEGIN
    IF ((NEW.post_id IS NOT NULL) + (NEW.auction_id IS NOT NULL) + (NEW.order_id IS NOT NULL)) > 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only one of post_id/auction_id/order_id may be non-NULL';
    END IF;

    IF (NEW.post_id    IS NOT NULL AND NEW.room_kind <> 'POST')
        OR (NEW.auction_id IS NOT NULL AND NEW.room_kind <> 'AUCTION')
        OR (NEW.order_id   IS NOT NULL AND NEW.room_kind <> 'ORDER') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'room_kind must match the non-NULL resource column';
    END IF;
END;

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
    user_no    int                                  not null,
    post_id    int                                  not null,
    created_at datetime default current_timestamp() null,
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
    user_id     varchar(20)                          not null,
    user_email  varchar(50)                          null,
    withdraw_at datetime default current_timestamp() not null
);




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


