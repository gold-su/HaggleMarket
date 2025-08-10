package com.hagglemarket.marketweb.category.repository;

import com.hagglemarket.marketweb.category.entity.Category;
import com.hagglemarket.marketweb.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Integer parentId);
}
