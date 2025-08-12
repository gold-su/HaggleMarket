package com.hagglemarket.marketweb.auction.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AuctionImageRequestDto {

    @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다.")
    private List<MultipartFile> images; //실제 이미지 파일

    // 파일별 정렬 순서 (옵션). images와 길이를 맞추면 해당 순서 적용, 아니면 기본 (i+1)
    private List<Integer> sortOrder;
}
