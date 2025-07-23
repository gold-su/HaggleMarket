package com.hagglemarket.marketweb.post.service;

import com.hagglemarket.marketweb.post.dto.PostCardDto;
import com.hagglemarket.marketweb.post.dto.PostDetailResponse;
import com.hagglemarket.marketweb.post.dto.PostRequestDto;
import com.hagglemarket.marketweb.post.dto.PostResponseDto;
import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.entity.PostImage;
import com.hagglemarket.marketweb.post.repository.PostImageRepository;
import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;

    public PostResponseDto createPost(PostRequestDto dto) {
        int userNo = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserNo();

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .cost(dto.getCost())
                .productStatus(dto.getProductStatus())
                .negotiable(dto.isNegotiable())
                .swapping(dto.isSwapping())
                .deliveryFee(dto.isDeliveryFee())
                .status(Post.Poststatus.FOR_SALE)
                .hit(0)
                .user(userRepository.findById(userNo)
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")))
                .build();

        // 🔄 이미지 URL 리스트만 처리
        if (dto.getImageUrls() != null) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                String imageUrl = dto.getImageUrls().get(i);
                PostImage postImage = PostImage.builder()
                        .imageUrl(imageUrl)
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
                .productStatus(saved.getProductStatus().name())
                .negotiable(saved.isNegotiable())
                .swapping(saved.isSwapping())
                .deliveryFee(saved.isDeliveryFee())
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

    public Page<PostCardDto> getPostCards(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(post -> {
            String thumbnail = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();

            return PostCardDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .cost(post.getCost())
                    .thumbnail(thumbnail)
                    .status(post.getProductStatus()) // enum 그대로 사용
                    .liked(false)                    // 추후 로그인 유저 좋아요 연동
                    .tags(null)                      // 추후 태그 연동
                    .build();
        });
    }

    public PostDetailResponse getPostDetail(int postId, Integer viewerUserNo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        boolean isMine = viewerUserNo != null && post.getUser().getUserNo() == viewerUserNo;

        List<String> imageUrls = postImageRepository.findImageUrlsByPostId(postId);

        return PostDetailResponse.from(post, isMine, imageUrls);
    }
}