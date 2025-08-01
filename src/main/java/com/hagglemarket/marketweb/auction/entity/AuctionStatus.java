package com.hagglemarket.marketweb.auction.entity;


//경매 상태를 표한하는 Enum (Enum은 자바 열거형 정의 키워드)
public enum  AuctionStatus {
    READY,      //등록됨 (시작 전)
    ONGOING,    //경매 진행 중
    ENDED,      //경매 종료됨
    CANCELLED   //경매 취소됨
}
