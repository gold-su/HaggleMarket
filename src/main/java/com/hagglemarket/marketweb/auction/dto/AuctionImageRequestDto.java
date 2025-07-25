package com.hagglemarket.marketweb.auction.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AuctionImageRequestDto {

    private MultipartFile imageFile; //실제 이미지 파일
    private int sortOrder;           //정렬 순서
}
