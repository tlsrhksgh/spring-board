package com.single.springboard.web.dto.comments;

public record CommentsResponse(
        Long id,
        String author,
        String content
) {
}
