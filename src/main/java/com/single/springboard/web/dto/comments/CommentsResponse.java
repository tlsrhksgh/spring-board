package com.single.springboard.web.dto.comments;

import com.single.springboard.domain.comment.Comment;
import lombok.Builder;

@Builder
public record CommentsResponse(
        Long id,
        String author,
        String content,
        Comment parentId,
        int commentLevel,
        String createdDate
) {
}
