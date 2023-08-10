package com.single.springboard.web.dto.posts;

import lombok.Builder;

@Builder
public record SearchResponse(
        Long id,
        String title,
        String content,
        String author,
        String modifiedDate
) {
}
