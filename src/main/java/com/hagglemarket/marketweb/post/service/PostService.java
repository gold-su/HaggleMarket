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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto dto) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int userNo = userDetails.getUserNo();  // ✅ 반드시 PK 꺼내기

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .cost(dto.getCost())
                .status(Post.Poststatus.FOR_SALE)
                .hit(0)
                .user_no(userNo)  // ✅ 필드명 맞춰서 PK 연결!
                .build();

        if (dto.getImageUrls() != null) {
            List<String> imageUrls = dto.getImageUrls();
            if (!imageUrls.isEmpty()) {
                for (int i = 0; i < imageUrls.size(); i++) {
                    String url = imageUrls.get(i);
                    PostImage postImage = PostImage.builder()
                            .imageUrl(url)
                            .sortOrder(i + 1)
                            .post(post)
                            .build();
                    post.getImages().add(postImage);
                }
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