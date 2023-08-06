package com.single.springboard.domain.posts;

import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comments.Comments;
import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.user.User;
import com.single.springboard.web.dto.posts.PostUpdateRequest;
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
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Files> files;

    @OneToMany(mappedBy = "posts", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comments> comments;

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
