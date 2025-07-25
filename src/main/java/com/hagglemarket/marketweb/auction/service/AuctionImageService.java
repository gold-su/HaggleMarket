package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service //스프링이 관리하는 서비스 컴포넌트로 등록
@RequiredArgsConstructor //생성자 자동 주입
public class AuctionImageService {

    private final AuctionImageRepository auctionImageRepository;

    /**
     * 경매 이미지 저장 메서드
     * @param files MultipartFile 리스트 (프론트에서 업로드한 이미지)
     * @param auctionPost 어떤 경매 상품에 속하는 이미지인지
     * @return 저장된 AuctionImage 리스트
     */

    @Transactional //이미지 저장 중 예외가 생기면 롤백 되도록 명시
    //경매 상품 이미지들을 DB에 저장하는 메서드
    // MultipartFile files = 프론트에서 업로드한 이미지들
    // AuctionPost auctionPost = 어떤 경매 상품의 이미지인지 연결하려고 받는 매개변수
    //리턴값: 저장이 완료된 AuctionImage 객체들의 리스트를 반환. 나중에 다시 활용 & 응답에 포함 가능
    public List<AuctionImage> saveImages(List<MultipartFile> files, AuctionPost auctionPost) {

        //이미지를 하나씩 저장한 후, 그 결과(AuctionImage)를 담아둘 리스트
        List<AuctionImage> savedImages = new ArrayList<>();

        //하나씩 이미지 파일을 꺼내서 -> AuctionImage 객체로 만들고 -> DB에 저장하고 -> 리스트에 추가하는 구조
        for(int i = 0; i < files.size(); i++) { //files의 size 만큼 반복

            MultipartFile file = files.get(i);

            try {
                AuctionImage image = AuctionImage.builder()
                        .auctionPost(auctionPost)       //어떤 상품의 이미지인지 지정
                        .imageData(file.getBytes())     //이미지 데이터를 byte[]로 저장
                        .imageName(file.getOriginalFilename())  //원본 파일명
                        .imageType(file.getContentType())   //MITE 타입 (IMAGE/JPEG 등)
                        .sortOrder(i + 1)               //순서 지정
                        .build();

                //JPA 레포지토리로 save
                savedImages.add(auctionImageRepository.save(image));
            }catch(IOException e){
                //런타임 예외 던지기 (이미지 저장 실패)
                throw new RuntimeException("이미지 저장 실패: " + file.getOriginalFilename(), e);
            }
        }

        //저장된 이미지 리스트 리턴
        return savedImages;
    }
}
