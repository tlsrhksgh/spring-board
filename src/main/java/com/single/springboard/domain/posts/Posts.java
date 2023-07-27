package com.single.springboard.domain.posts;

import com.single.springboard.domain.BaseTimeEntity;
import com.single.springboard.domain.comments.Comments;
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

    private String author;

    @OneToMany(mappedBy = "posts", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Comments> comments;

    private int commentCount;

    public void updatePost(PostUpdateRequest updateDto) {
        this.title = updateDto.title();
        this.content = updateDto.content();
    }
}
