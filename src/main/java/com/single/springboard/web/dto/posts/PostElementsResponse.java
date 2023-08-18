package com.single.springboard.web.dto.posts;

import com.single.springboard.web.dto.comments.CommentsResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record PostElementsResponse(
        Long id,
        String author,
        String content,
        String title,
        List<CommentsResponse> comments,
        List<String> fileName
) {
}
