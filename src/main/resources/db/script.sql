/*
DROP  DATABASE hagglemarket;
create database hagglemarket;
use hagglemarket;
*/

#일단 user 테이블만 생성 / 다른 기능 구현할 때 차차 생성

use hagglemarket;
create table user
(
    user_no      int auto_increment
        primary key,
    user_id      varchar(20)  not null,
    user_name    varchar(10)  not null,
    password    varchar(255) not null,
    phone_number varchar(11)     not null,
    nick_name    varchar(15)  not null,
    address     varchar(30)  not null,
    email       varchar(50)  not null,
    image_url    text         null,
    created_at     datetime     null,
    status      varchar(20)  null,
    rating      decimal      null,
    road_rating  decimal      null
);
#밑부터 수정해야함

create table category
(
    categoryId   int auto_increment
        primary key,
    categoryName varchar(20) not null
);


create table auctionPost
(
    auctionPostId int auto_increment
        primary key,
    userNo        int         not null,
    stockCost     int         not null,
    categoryId    int         not null,
    title         varchar(50) not null,
    startCost     int         not null,
    endCost       int         null,
    content       text        not null,
    created       datetime    null,
    updated       datetime    null,
    status        varchar(10) null,
    constraint auctionPost_categoryId_categoryId_fk
        foreign key (categoryId) references category (categoryId),
    constraint auctionPost_user_userId_fk
        foreign key (userNo) references user (userNo)
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


