package com.hagglemarket.marketweb.post.controller;

import com.hagglemarket.marketweb.post.dto.PostRequestDto;
import com.hagglemarket.marketweb.post.dto.PostResponseDto;
import com.hagglemarket.marketweb.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto,
                                                      @RequestPart("images") List<MultipartFile> images) {
        PostResponseDto postResponseDto = postService.createPost(postRequestDto, images);
        return ResponseEntity.ok(postResponseDto);
    }
}
