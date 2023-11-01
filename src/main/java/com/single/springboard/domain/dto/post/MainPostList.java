package com.single.springboard.domain.dto.post;

public record MainPostList(
        Long id,
        String title,
        String author,
        Long commentCount,
        Long viewCount,
        String modifiedDate
) {

}
