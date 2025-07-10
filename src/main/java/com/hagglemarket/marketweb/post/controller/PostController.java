package com.hagglemarket.marketweb.post.controller;

import com.hagglemarket.marketweb.post.dto.PostRequestDto;
import com.hagglemarket.marketweb.post.dto.PostResponseDto;
import com.hagglemarket.marketweb.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto postResponseDto = postService.createPost(postRequestDto);
        return ResponseEntity.ok(postResponseDto);
    }
}
