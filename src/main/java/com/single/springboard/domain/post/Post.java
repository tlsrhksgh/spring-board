package com.single.springboard.domain.post;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.dto.post.PostUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_post_title", columnList = "title"))
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(updatable = false, nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @JsonManagedReference(value = "post-files")
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<File> files;

    @JsonManagedReference(value = "post-comments")
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToOne
    @JsonBackReference(value = "post-user")
    @JoinColumn(name = "user_id")
    private User user;

    private long viewCount;

    public void updatePost(PostUpdateRequest updateRequest) {
        this.title = updateRequest.title();
        this.content = updateRequest.content();
    }
}
