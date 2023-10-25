package com.single.springboard.domain.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.post.Post;
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
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(nullable = false, updatable = false)
    private String author;

    private boolean secret;

    private int commentLevel;

    @JsonBackReference(value = "post-comments")
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonBackReference(value = "user-comments")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonBackReference(value = "parent-child_comment")
    @ManyToOne
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "id")
    private Comment parentComment;

    @JsonManagedReference(value = "parent-child_comment")
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();
}
