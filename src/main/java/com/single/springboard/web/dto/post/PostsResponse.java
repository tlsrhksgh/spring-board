package com.single.springboard.web.dto.post;


import lombok.Builder;

@Builder
public record PostsResponse(
        Long id,
        String title,
        String author,
        long commentsCount,
        long viewCount,
        String modifiedDate
) {

}
