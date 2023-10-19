package com.single.springboard.domain.post.dto;


import lombok.Builder;

@Builder
public record MainPostList(
        Long id,
        String title,
        String author,
        Long commentCount,
        Long viewCount,
        String modifiedDate
) {

}
