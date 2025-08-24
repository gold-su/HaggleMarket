package com.hagglemarket.marketweb.auction.service;

import com.hagglemarket.marketweb.auction.dto.AuctionImageRequestDto;
import com.hagglemarket.marketweb.auction.dto.AuctionImageResponseDto;
import com.hagglemarket.marketweb.auction.entity.AuctionImage;
import com.hagglemarket.marketweb.auction.entity.AuctionPost;
import com.hagglemarket.marketweb.auction.repository.AuctionImageRepository;
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

    @Transactional //이미지 저장 중 예외가 생기면 롤백 되도록 명시
    //경매 상품 이미지들을 DB에 저장하는 메서드
    //MultipartFile files = 프론트에서 업로드한 이미지들
    //AuctionPost auctionPost = 어떤 경매 상품의 이미지인지 연결하려고 받는 매개변수
    //리턴값: 저장이 완료된 AuctionImage 객체들의 리스트를 반환. 나중에 다시 활용 & 응답에 포함 가능
    public List<AuctionImageResponseDto.ImageInfo> saveImages(AuctionImageRequestDto req, AuctionPost auctionPost) {
        List<MultipartFile> files = req.getImages(); //업로드된 파일 목록
        List<Integer> orders = req.getSortOrder();   //파일별 정렬 순서(없으면 null)

        if(files == null || files.isEmpty()) {
            throw new IllegalArgumentException("이미지는 최소 1개 이상 업로드해야 합니다.");
        }

        if(files.size() > 12) {
            throw new IllegalArgumentException("이미지는 최대 12개까지 업로드 가능합니다.");
        }


        List<AuctionImageResponseDto.ImageInfo> results = new ArrayList<>(); //응답 DTO로 보낼 이미지 정보 저장용


        //이미지를 하나씩 저장한 후, 그 결과(AuctionImage)를 담아둘 리스트
        //List<AuctionImage> savedImages = new ArrayList<>();

        //하나씩 이미지 파일을 꺼내서 -> AuctionImage 객체로 만들고 -> DB에 저장하고 -> 리스트에 추가하는 구조
        for(int i = 0; i < files.size(); i++) { //files의 size 만큼 반복
            //현재 처리 중인 파일
            MultipartFile file = files.get(i);
            //정렬 순서가 요청에 있으면 그 값 사용
            //없으면 인덱스(i+1)로 기본 설정
            //즉, 정렬 순서 리스트가 존재하고, 현재 파일에 해당하는 순서 값이 들어있다면 -> 참
            // -> 참일 때 실행 orders.get(i) = orders 리스트에서 현재 인덱스(i)에 해당하는 값을 가져옴.
            // -> 거짓일 때 실행 (i + 1) = 리스트가 없거나(i보다 길이가 짧거나) → 기본값으로 현재 인덱스+1 사용.
            int sortOrder = (orders != null && orders.size() > i) ? orders.get(i) : (i + 1);
            try {
                AuctionImage saved = auctionImageRepository.save(
                        AuctionImage.builder()
                            .auctionPost(auctionPost)       //어떤 상품의 이미지인지 지정
                            .imageData(file.getBytes())     //이미지 데이터를 byte[]로 저장
                            .imageName(file.getOriginalFilename())  //원본 파일명
                            .imageType(file.getContentType())   //MITE 타입 (IMAGE/JPEG 등)
                            .sortOrder(sortOrder)               //순서 지정
                            .build()
                );
                //저장된 이미지의 ID, 이름, 타입, 크기 등을 기반으로 ImageInfo DTO 생성
                results.add(
                        AuctionImageResponseDto.ImageInfo.builder()
                                .imageId(saved.getImageId())
                                .imageUrl(saved.getImageUrl()) // 이미지 조회 URL은 imageId 기반으로 계산
                                .imageName(saved.getImageName())
                                .imageType(saved.getImageType())
                                .sortOrder(saved.getSortOrder())
                                .size(saved.getImageData() == null ? 0L : saved.getImageData().length) //byte 배열의 길이로 파일 크기 계산
                                .build()
                );

            }catch(IOException e){
                //런타임 예외 던지기 (이미지 저장 실패)
                throw new RuntimeException("이미지 저장 실패: " + file.getOriginalFilename(), e);
            }
        }

        //모든 파일을 처리한 뒤, 응답 DTO 리스트 반환
        return results;
    }
}
