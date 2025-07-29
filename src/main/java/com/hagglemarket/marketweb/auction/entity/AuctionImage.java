package com.hagglemarket.marketweb.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity //엔티티 등록
@Table(name = "auction_post_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionImage {

    @Id //pk 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
    @Column(name = "image_id", nullable = false)
    private int imageId;

    @ManyToOne(fetch = FetchType.LAZY) //n:1 관게 설정 / 여러 이미지가 하나의 경매 상품에 연결
    @JoinColumn(name = "auction_id", nullable = false)
    private AuctionPost auctionPost; // 이 필드는 이미지가 어떤 경매 상품에 속해 있는가를 나태는 거임. 즉, 이미지의 부모는 AuctionPost 이고, auction_id 외래키로 연결됨

    @Lob //대용량 데이터(LONGBLOB) 매핑
    @Column(name = "image_data", nullable = false, columnDefinition = "MEDIUMBLOB") //blob 으로 인식하게 명시
    private byte[] imageData;

    @Column(name = "image_name", nullable = false, length = 255)
    private String imageName;

    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
