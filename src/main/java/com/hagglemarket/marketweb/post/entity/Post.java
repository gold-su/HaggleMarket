package com.hagglemarket.marketweb.post.entity;

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

    @Column(name="user_no",nullable = false)
    private int user_no;

    @Column(name="title",nullable = false,length = 50)
    private String title;

    @Column(name="cost",nullable = false)
    private int cost;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();
}
