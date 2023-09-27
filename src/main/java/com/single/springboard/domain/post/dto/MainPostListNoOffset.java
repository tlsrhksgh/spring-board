package com.single.springboard.domain.post.dto;


import lombok.Builder;

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
