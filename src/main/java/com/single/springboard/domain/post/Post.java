package com.single.springboard.domain.post;

import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.file.File;
import com.single.springboard.domain.user.User;
import com.single.springboard.web.dto.post.PostUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<File> files;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private long viewCount;

    public void updatePost(PostUpdateRequest updateDto) {
        this.title = updateDto.title();
        this.content = updateDto.content();
    }

    public void updateViewCount() {
        this.viewCount += 1;
    }
}