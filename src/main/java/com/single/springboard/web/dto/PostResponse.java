package com.single.springboard.web.dto;


import com.single.springboard.domain.posts.Posts;

public record PostResponse(
        Long id,
        String author,
        String content,
        String title
) {
}
