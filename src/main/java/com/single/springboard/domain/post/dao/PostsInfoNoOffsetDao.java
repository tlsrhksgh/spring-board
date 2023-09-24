package com.single.springboard.domain.post.dao;


import lombok.Builder;

@Builder
public record PostsInfoNoOffsetDao(
        Long id,
        String title,
        String author,
        Long commentCount,
        Long viewCount,
        String modifiedDate
) {

}
