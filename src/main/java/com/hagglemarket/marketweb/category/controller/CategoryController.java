package com.hagglemarket.marketweb.category.controller;

import com.hagglemarket.marketweb.category.dto.CategoryDto;
import com.hagglemarket.marketweb.category.entity.Category;
import com.hagglemarket.marketweb.category.repository.CategoryRepository;
import com.hagglemarket.marketweb.category.service.CategoryService;
import com.hagglemarket.marketweb.post.dto.PostCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    // 1. 대분류 목록 (parent_id = NULL)
    @GetMapping("/roots")
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    // 2. 특정 parent의 하위 카테고리 목록
    @GetMapping("/{parentId}")
    public List<Category> getChildren(@PathVariable Integer parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    // 3. 단일 카테고리 상세 (수정 시 3단계 세팅용)
    @GetMapping("/detail/{id}")
    public Category getCategoryDetail(@PathVariable Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
    }

    @GetMapping("/{categoryId}/posts")
    public ResponseEntity<List<PostCardDto>> getPostsByCategoryId(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(categoryService.getPostsByCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}