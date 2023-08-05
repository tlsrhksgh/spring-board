package com.single.springboard.domain.files;

import com.single.springboard.domain.posts.Posts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    private String originalName;
    private String translateName;
    private long size;

    @CreatedDate
    private LocalDateTime createdDate;
}
