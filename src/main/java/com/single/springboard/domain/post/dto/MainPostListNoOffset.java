package com.single.springboard.domain.post.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MainPostListNoOffset(
        Long id,
        String title,
        String author,
        Long commentCount,
        Long viewCount,
        String modifiedDate
) {

}
