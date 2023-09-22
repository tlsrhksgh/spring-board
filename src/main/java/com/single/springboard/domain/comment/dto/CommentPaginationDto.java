package com.single.springboard.domain.comment.dto;

public record CommentPaginationDto(
        Long commentId,
        Long postId,
        String postTitle,
        String content,
        String createdDate,
        Integer childrenCount
) {
}
