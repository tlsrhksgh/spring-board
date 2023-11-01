package com.single.springboard.domain.dto.comment;

public record CommentPaginationDto(
        Long commentId,
        Long postId,
        String postTitle,
        String content,
        String createdDate,
        Integer childrenCount
) {
}
