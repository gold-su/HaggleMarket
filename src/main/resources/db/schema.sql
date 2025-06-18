


create table category
(
    categoryId   int auto_increment
        primary key,
    categoryName varchar(20) not null
);

create table user
(
    userId      int auto_increment
        primary key,
    phoneNumber char(11)    not null,
    nickName    varchar(15) not null,
    address     varchar(30) not null,
    email       varchar(50) not null,
    imageURL    text        not null,
    `create`    datetime    null,
    status      varchar(20) null,
    rating      decimal     null,
    roadRating  decimal     null
);

create table auctionPost
(
    auctionPostId int auto_increment
        primary key,
    userId        int         not null,
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
        foreign key (userId) references user (userId)
);

create table bidder
(
    bidderId      int auto_increment
        primary key,
    auctionPostId int      not null,
    userId        int      not null,
    bidCost       int      not null,
    created       datetime null,
    constraint bidder_auctionPost_auctionPostId_fk
        foreign key (auctionPostId) references auctionpost (auctionPostId),
    constraint bidder_user_userId_fk
        foreign key (userId) references user (userId)
);

create table post
(
    postId     int auto_increment
        primary key,
    userId     int         not null,
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
        foreign key (userId) references user (userId)
);

create table chattingGroup
(
    chattingRoomId int auto_increment
        primary key,
    postId         int         not null,
    sellerId       int         not null,
    buyerId        int         not null,
    created        datetime    null,
    updated        datetime    null,
    status         varchar(10) null,
    constraint chattingGroup_post_postId_fk
        foreign key (postId) references post (postId),
    constraint chattingGroup_user_userId_fk
        foreign key (sellerId) references user (userId),
    constraint chattingGroup_user_userId_fk_2
        foreign key (buyerId) references user (userId)
);

create table chattingContent
(
    chattingRoomId    int         not null,
    chattingContentId int auto_increment
        primary key,
    userId            int         not null,
    created           datetime    null,
    updated           datetime    null,
    status            varchar(10) null,
    constraint chattingContent_chattingGroup_chattingRoomId_fk
        foreign key (chattingRoomId) references chattingGroup (chattingRoomId),
    constraint chattingContent_user_userId_fk
        foreign key (userId) references user (userId)
);

create table endDeal
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
        foreign key (sellerId) references user (userId),
    constraint endDeal_user_userId_fk_2
        foreign key (buyerId) references user (userId)
);

create table postImage
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

create table searchHistory
(
    searchId   int auto_increment
        primary key,
    userId     int         not null,
    searchText varchar(60) not null,
    constraint searchHistory_user_userId_fk
        foreign key (userId) references user (userId)
);

create table wishList
(
    wishListId int auto_increment
        primary key,
    userId     int      not null,
    postId     int      not null,
    created    datetime null,
    updated    datetime null,
    constraint wishList_user_userId_fk
        foreign key (userId) references user (userId),
    constraint wishList_post_userId_fk_2
        foreign key (postId) references post (postId)
);


