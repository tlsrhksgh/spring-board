package com.single.springboard.web.dto.post;

import com.single.springboard.web.dto.comment.CommentsResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record
PostDetailResponse(
        Long id,
        String author,
        String content,
        String title,
        List<CommentsResponse> comments,
        List<String> fileName
) {
}
