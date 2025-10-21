package com.hagglemarket.marketweb.post.controller;

import com.hagglemarket.marketweb.post.dto.*;
import com.hagglemarket.marketweb.post.service.PostService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시물 등록
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto postResponseDto = postService.createPost(postRequestDto);
        return ResponseEntity.ok(postResponseDto);
    }

    // 이미지 업로드
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : images) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadPath = "C:/uploads/" + fileName;

            try {
                file.transferTo(new File(uploadPath));
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }

            imageUrls.add("/uploads/" + fileName);
        }

        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping
    public Page<PostCardDto> getPosts(@PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getPostCards(pageable);
    }

    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer userNo = userDetails != null ? userDetails.getUserNo() : null;

        PostDetailResponse response = postService.getPostDetail(postId, userNo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/hit")
    public ResponseEntity<Void> increaseHit(@PathVariable Integer postId,
                                            HttpServletRequest request) {
        postService.increaseHitWithSession(postId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) throws AccessDeniedException {

        postService.updatePost(postId, dto, user.getUserNo());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable int postId) {
        // 🔹 로그인한 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int userNo = userDetails.getUserNo();

        // 🔹 삭제 처리 실행
        postService.deletePost(postId, userNo);

        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

}
