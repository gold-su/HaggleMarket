package com.hagglemarket.marketweb.post.service;

import com.hagglemarket.marketweb.post.dto.PostRequestDto;
import com.hagglemarket.marketweb.post.dto.PostResponseDto;
import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.entity.PostImage;
import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto dto,List<MultipartFile> images) {
        int userNo = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserNo();

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .cost(dto.getCost())
                .status(Post.Poststatus.FOR_SALE)
                .hit(0)
                .user_no(userNo)  // ✅ 필드명 맞춰서 PK 연결!
                .build();

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String uploadPath = "C:/upload/" + fileName; // 원하는 서버 경로

                try {
                    file.transferTo(new File(uploadPath)); // 로컬에 저장
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }

                PostImage postImage = PostImage.builder()
                        .imageUrl("/upload/" + fileName) // DB엔 상대경로 or 접근 가능한 URL 저장
                        .sortOrder(i + 1)
                        .post(post)
                        .build();

                post.getImages().add(postImage);
            }
        }

        Post saved = postRepository.save(post);

        return PostResponseDto.builder()
                .postId(saved.getPostId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .cost(saved.getCost())
                .status(saved.getStatus())
                .hit(saved.getHit())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .imageUrls(
                        saved.getImages().stream()
                                .map(PostImage::getImageUrl)
                                .toList()
                )
                .build();
    }
}