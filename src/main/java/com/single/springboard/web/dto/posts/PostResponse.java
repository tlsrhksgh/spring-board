package com.single.springboard.web.dto.posts;

import lombok.Builder;

@Builder
public record PostResponse(
        Long id,
        String title,
        String author,
        String content
) {
}
