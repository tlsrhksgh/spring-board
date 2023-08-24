package com.single.springboard.web.dto.post;

public record SearchResponse(
        Long id,
        String title,
        String content,
        String author,
        String modifiedDate
) {
}
