package com.hagglemarket.marketweb.category.service;

import com.hagglemarket.marketweb.category.dto.CategoryDto;
import com.hagglemarket.marketweb.category.entity.Category;
import com.hagglemarket.marketweb.category.repository.CategoryRepository;
import com.hagglemarket.marketweb.post.dto.PostCardDto;
import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public List<PostCardDto> getPostsByCategory(Integer categoryId) {
        List<Post> posts = postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);

        return posts.stream()
                .map(post -> PostCardDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .cost(post.getCost())
                        .thumbnail(post.getImages() != null && !post.getImages().isEmpty()
                                ? post.getImages().get(0).getImageUrl()
                                : null)
                        .status(post.getProductStatus())
                        .liked(false)  // 나중에 로그인 사용자 기반으로 변경 가능
                        .tags(null)
                        .build())
                .toList();
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .parentId(category.getParent() != null ? category.getParent().getId() : null)
                        .build())
                .toList();
    }
}
