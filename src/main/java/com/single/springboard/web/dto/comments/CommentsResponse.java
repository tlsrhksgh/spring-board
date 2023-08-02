package com.single.springboard.web.dto.comments;

import com.single.springboard.domain.comments.Comments;
import lombok.Builder;

@Builder
public record CommentsResponse(
        Long id,
        String author,
        String content,
        Comments parentId,
        int commentLevel
) {
}
