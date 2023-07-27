package com.single.springboard.domain.comments;

import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = @Index(name = "i_comments", columnList = "posts_id"))
@Entity
public class Comments extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private boolean secret;

    private int replyLevel;
    private Long parentId;
}