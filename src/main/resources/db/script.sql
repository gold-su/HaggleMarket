/*
DROP  DATABASE hagglemarket;
create database hagglemarket;
use hagglemarket;
*/

#일단 user 테이블만 생성 / 다른 기능 구현할 때 차차 생성

use hagglemarket;

create table users
(
    user_no      int auto_increment
        primary key,
    user_id      varchar(20)  not null,
    user_name    varchar(20)  not null,
    password     varchar(255) not null,
    phone_number varchar(11)  not null,
    nick_name    varchar(15)  not null,
    address      varchar(30)  not null,
    email        varchar(50)  not null,
    image_url    text         null,
    created_at   datetime     null,
    status       varchar(20)  null,
    rating       decimal      null,
    road_rating  decimal      null
);

create table posts
(
    post_id    int auto_increment
        primary key,
    user_no    int                                not null,
    title      varchar(50)                        not null,
    cost       int                                not null,
    content    text                               not null,
    hit        int      default 0                 not null,
    created_at datetime default (now())           null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    status     varchar(20)                        null,
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

create table withdrawusers
(
    no          int auto_increment
        primary key,
    user_id     varchar(20)                        not null,
    user_email  varchar(50)                        null,
    withdraw_at datetime default CURRENT_TIMESTAMP not null
);

USE hagglemarket;

-- 경매 상품 테이블
CREATE TABLE auction_posts
(
    auction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_no INT NOT NULL,                     -- 판매자
    category_id INT NULL,                 -- 카테고리
    title VARCHAR(50) NOT NULL,               -- 제목
    content TEXT NOT NULL,                    -- 내용

    start_cost INT NOT NULL,                  -- 시작가
    current_cost INT NOT NULL,                -- 현재가
    buyout_cost INT NULL,                     -- 즉시구매가 (선택사항)

    start_time DATETIME NOT NULL,             -- 경매 시작시간
    end_time DATETIME NOT NULL,               -- 경매 종료시간

    winner_user_no INT NULL,                  -- 낙찰자 user_no

    hit INT DEFAULT 0 NOT NULL,               -- 조회수
    bid_count INT DEFAULT 0 NOT NULL,         -- 입찰 횟수

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,                             -- 등록일
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
    status ENUM('READY', 'ONGOING', 'ENDED', 'CANCELLED') DEFAULT 'READY',     -- 경매 상태

    CONSTRAINT fk_auction_posts_user
        FOREIGN KEY (user_no) REFERENCES users (user_no),
    #     CONSTRAINT fk_auction_posts_category
        #         FOREIGN KEY (category_id) REFERENCES category (category_id),
    CONSTRAINT fk_auction_posts_winner
        FOREIGN KEY (winner_user_no) REFERENCES users (user_no)
);

-- 경매 상품 이미지 테이블
CREATE TABLE auction_post_images
(
    image_id INT AUTO_INCREMENT PRIMARY KEY,       -- 이미지 아이디
    auction_id INT NOT NULL,                       -- 경매 아이디
    image_data mediumblob NOT NULL,                  -- 이미지 바이너리 데이터
    image_name VARCHAR(255) NOT NULL,              -- 원본 이미지 이름
    image_type VARCHAR(50) NOT NULL,               -- 이미지 타입 (ex: image/png)
    sort_order INT NOT NULL,                       -- 이미지 순서
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜

    CONSTRAINT fk_auction_post_images
        FOREIGN KEY (auction_id) REFERENCES auction_posts (auction_id)
            ON DELETE CASCADE
);

-- 입찰 내역 테이블
CREATE TABLE bids
(
    bid_id INT AUTO_INCREMENT PRIMARY KEY,
    auction_id INT NOT NULL,           -- 경매 상품 ID (FK)
    bidder_user_no INT NOT NULL,       -- 입찰자 user_no (FK)
    bid_amount INT NOT NULL,           -- 입찰 금액
    bid_time DATETIME DEFAULT CURRENT_TIMESTAMP, -- 입찰 시각

    CONSTRAINT fk_bids_auction_posts
        FOREIGN KEY (auction_id) REFERENCES auction_posts (auction_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_bids_user
        FOREIGN KEY (bidder_user_no) REFERENCES users (user_no)
            ON DELETE CASCADE
);



#밑부터 수정해야함

create table category
(
    categoryId   int auto_increment
        primary key,
    categoryName varchar(20) not null
);


create table bidder
(
    bidderId      int auto_increment
        primary key,
    auctionPostId int      not null,
    userNo        int      not null,
    bidCost       int      not null,
    created       datetime null,
    constraint bidder_auctionPost_auctionPostId_fk
        foreign key (auctionPostId) references auctionpost (auctionPostId),
    constraint bidder_user_userId_fk
        foreign key (userNo) references user (userNo)
);

create table post
(
    postId     int auto_increment
        primary key,
    userNo     int         not null,
    categoryId int         not null,
    title      varchar(50) not null,
    cost       int         not null,
    content    text        not null,
    hit        int         null,
    created    datetime    null,
    updated    datetime    null,
    status     varchar(10) null,
    constraint post_categoryId_categoryId_fk
        foreign key (categoryId) references category (categoryId),
    constraint post_user_userId_fk
        foreign key (userNo) references user (userNo)
);

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

create table enddeal
(
    endPostId int auto_increment
        primary key,
    postId    int         not null,
    sellerId  int         not null,
    buyerId   int         not null,
    created   datetime    null,
    updated   datetime    null,
    status    varchar(10) null,
    constraint endDeal_post_postId_fk
        foreign key (postId) references post (postId),
    constraint endDeal_user_userId_fk
        foreign key (sellerId) references user (userNo),
    constraint endDeal_user_userId_fk_2
        foreign key (buyerId) references user (userNo)
);

create table postimage
(
    postId      int         not null,
    postImageId int auto_increment
        primary key,
    imageURL    text        not null,
    created     datetime    null,
    updated     datetime    null,
    status      varchar(10) null,
    constraint postImage_post_postId_fk
        foreign key (postId) references post (postId)
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


create table users
(
    user_no      int auto_increment
        primary key,
    user_id      varchar(20)  not null,
    user_name    varchar(20)  not null,
    password     varchar(255) not null,
    phone_number varchar(11)  not null,
    nick_name    varchar(15)  not null,
    address      varchar(30)  not null,
    email        varchar(50)  not null,
    image_url    text         null,
    created_at   datetime     null,
    status       varchar(20)  null,
    rating       decimal      null,
    road_rating  decimal      null
);

create table posts
(
    post_id    int auto_increment
        primary key,
    user_no    int                                not null,
    title      varchar(50)                        not null,
    cost       int                                not null,
    content    text                               not null,
    hit        int      default 0                 not null,
    created_at datetime default (now())           null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    status     varchar(20)                        null,
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

create table withdrawusers
(
    no          int auto_increment
        primary key,
    user_id     varchar(20)                        not null,
    user_email  varchar(50)                        null,
    withdraw_at datetime default CURRENT_TIMESTAMP not null
);