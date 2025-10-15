package com.hagglemarket.marketweb.post.service;

import com.hagglemarket.marketweb.category.entity.Category;
import com.hagglemarket.marketweb.category.repository.CategoryRepository;
import com.hagglemarket.marketweb.post.dto.*;
import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.entity.PostImage;
import com.hagglemarket.marketweb.post.repository.PostImageRepository;
import com.hagglemarket.marketweb.post.repository.PostRepository;
import com.hagglemarket.marketweb.postlike.repository.PostLikeRepository;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import com.hagglemarket.marketweb.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponseDto createPost(PostRequestDto dto) {
        int userNo = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserNo();

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
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

        // 이미지 URL 리스트만 처리
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
                .categoryId(saved.getCategoryId())
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

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(int postId, Integer viewerUserNo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        boolean isMine = viewerUserNo != null && post.getUser().getUserNo() == viewerUserNo;

        // === 찜 관련 ===
        boolean likedByMe = false;
        int likeCount = (int) postLikeRepository.countByPostId(postId); // ✅ postId 기반

        if (viewerUserNo != null) {
            likedByMe = postLikeRepository.existsByUserNoAndPostId(viewerUserNo, postId); // ✅ userNo, postId 기반
        }

        // === 이미지 목록 ===
        List<String> imageUrls = postImageRepository.findImageUrlsByPostId(postId);

        // === 카테고리 경로 ===
        String categoryPath = null;
        if (post.getCategoryId() != null) {
            Category small = categoryRepository.findById(post.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
            Category middle = small.getParent();
            Category large = middle.getParent();
            categoryPath = large.getName() + " > " + middle.getName() + " > " + small.getName();
        }

        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .productStatus(post.getProductStatus().name())
                .cost(post.getCost())
                .negotiable(post.isNegotiable())
                .swapping(post.isSwapping())
                .deliveryFee(post.isDeliveryFee())
                .content(post.getContent())
                .hit(post.getHit())
                .createdAt(post.getCreatedAt())
                .status(post.getStatus().name())
                .isMine(isMine)
                .categoryId(post.getCategoryId())
                .categoryPath(categoryPath)
                .images(imageUrls)
                .seller(new PostDetailResponse.SellerInfo(post.getUser()))
                .likedByMe(likedByMe)
                .likeCount(likeCount)
                .build();
    }

//    @Transactional
//    public void increaseHit(Integer postId, HttpServletRequest request, CustomUserDetails user) {
//        try {
//            Post post = postRepository.findById(postId)
//                    .orElseThrow(() -> new RuntimeException("해당 게시물 없음"));
//
//            String userKey;
//            if (user != null) {
//                userKey = "USER_" + user.getUserNo(); // 로그인 사용자 기준
//            } else {
//                userKey = "IP_" + request.getRemoteAddr(); // 비로그인 → IP 기준
//            }
//
//            String redisKey = "post_hit:" + postId + ":" + userKey;
//
//            if (redisTemplate.hasKey(redisKey)) {
//                return;
//            }
//
//            post.increaseHit();
//            log.info(" 조회수 증가: postId={}, hit={}", postId, post.getHit());
//            redisTemplate.opsForValue().set(redisKey, "viewed", Duration.ofSeconds(10));
//
//        } catch (Exception e) {
//            log.error("조회수 증가 중 예외 발생: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
    @Transactional
    public void increaseHitWithSession(Integer postId, HttpServletRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("해당 게시물 없음"));

        HttpSession session = request.getSession();
        Object viewedObj = session.getAttribute("viewedPosts");

        Set<Integer> viewedPosts;

        if (viewedObj instanceof Set<?>) {
            // 경고 억제 및 캐스팅
            @SuppressWarnings("unchecked")
            Set<Integer> safeCast = (Set<Integer>) viewedObj;
            viewedPosts = safeCast;
        } else {
            viewedPosts = new HashSet<>();
        }

        if (!viewedPosts.contains(postId)) {
            post.increaseHit(); // 실제 조회수 증가
            viewedPosts.add(postId); // 세션에 저장
            session.setAttribute("viewedPosts", viewedPosts); // 세션 갱신
        }
    }

    @Transactional
    public void updatePost(int postId, PostUpdateRequestDto dto, int userNo) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 검증
        if (post.getUser().getUserNo() != userNo) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }

        // 1. 텍스트 필드 수정
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCost(dto.getCost());
        post.setCategoryId(dto.getCategoryId());
        post.setNegotiable(dto.isNegotiable());
        post.setSwapping(dto.isSwapping());
        post.setDeliveryFee(dto.isDeliveryFee());
        post.setProductStatus(dto.getProductStatus());

        // 2. 이미지 교체 로직
        // 기존 이미지 모두 삭제
        post.getImages().clear();

        // 새로운 이미지 URL 리스트를 순서대로 다시 추가
        if (dto.getImageUrls() != null) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                String url = dto.getImageUrls().get(i);
                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(url)
                        .sortOrder(i + 1)  // 1,2,3 순서 저장
                        .build();
                post.getImages().add(postImage);
            }
        }
    }

    public Integer getSellerUserNoByPostId(Integer postId) {
        return postRepository.findById(postId)
                .map(p -> p.getUser().getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
    }
}