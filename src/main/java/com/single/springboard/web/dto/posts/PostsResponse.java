package com.single.springboard.web.dto.posts;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostsResponse(
        Long id,
        String title,
        String author,
        long commentsCount,
        long viewCount,
        LocalDateTime modifiedDate
) {

}
