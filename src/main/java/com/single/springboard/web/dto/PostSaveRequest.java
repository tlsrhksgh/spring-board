package com.single.springboard.web.dto;

import com.single.springboard.domain.posts.Posts;

public record PostSaveRequest(
        String title,
        String content,
        String author
) {
    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
