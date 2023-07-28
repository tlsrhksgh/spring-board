package com.single.springboard.web.dto.comments;

import lombok.Builder;

@Builder
public record CommentsResponse(
        Long id,
        String author,
        String content,
        Long parentId,
        int commentLevel
) {
}
