package com.hagglemarket.marketweb.postlike.entity;

import com.hagglemarket.marketweb.post.entity.Post;
import com.hagglemarket.marketweb.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name="post_like",
        uniqueConstraints=@UniqueConstraint(name="uq_user_post", columnNames={"user_no","post_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="user_no",nullable = false)
    private int userNo;

    @Column(name="post_id",nullable = false)
    private int postId;

    @Column(name="created_at",insertable = false, updatable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private User user;
}
