package com.hagglemarket.marketweb.auction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AuctionPostUpdateRequest {

    @Size(min = 1, max = 50, message = "제목은 1~50자")
    private String title;

    @Size(min=1, message = "내용은 비어있을 수 없습니다.")
    private String content;

    @Min(value = 0,message = "즉시구매가는 0 이상이어야 합니다.")
    private Integer buyoutCost; //null 이면 `즉시구매 불가`로 간주


    private LocalDateTime startTime; //READY 상태에서만 변경 허용
    private LocalDateTime endTime;   //READY 상태 + startTime// < endTime

    private Integer categoryId;
}
