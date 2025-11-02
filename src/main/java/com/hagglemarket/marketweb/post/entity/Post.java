package com.hagglemarket.marketweb.post.entity;

import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(name="title",nullable = false,length = 50)
    private String title;

    public enum ProductStatus {
        NEW, USED_LIKE_NEW, USED_GOOD, USED, DAMAGED
    }

    @Column(name="category_id",nullable = false)
    private Integer categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus;

    @Column(name="cost",nullable = false)
    private int cost;

    @Column(name = "negotiable", nullable = false)
    private boolean negotiable;

    @Column(name = "swapping", nullable = false)
    private boolean swapping;

    @Column(name = "delivery_fee", nullable = false)
    private boolean deliveryFee;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "hit",nullable = false)
    private int hit;

    @Column(name = "created_at",updatable = false,insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Poststatus status;

    public enum Poststatus{
        FOR_SALE,
        SOLD_OUT,
        TRADING,
        DELETED
    }

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();
}
