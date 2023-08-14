package com.single.springboard.web.dto.posts;

public record SearchResponse(
        Long id,
        String title,
        String content,
        String author,
        String modifiedDate
) {
}
