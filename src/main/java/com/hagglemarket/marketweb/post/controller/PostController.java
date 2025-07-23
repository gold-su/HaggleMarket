package com.hagglemarket.marketweb.post.controller;

import com.hagglemarket.marketweb.post.dto.PostCardDto;
import com.hagglemarket.marketweb.post.dto.PostDetailResponse;
import com.hagglemarket.marketweb.post.dto.PostRequestDto;
import com.hagglemarket.marketweb.post.dto.PostResponseDto;
import com.hagglemarket.marketweb.post.service.PostService;
import com.hagglemarket.marketweb.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    public Page<PostCardDto> getPosts(@PageableDefault(size=8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
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
}
