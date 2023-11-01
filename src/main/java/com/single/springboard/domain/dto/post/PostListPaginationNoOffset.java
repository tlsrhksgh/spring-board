package com.single.springboard.domain.dto.post;

public record PostListPaginationNoOffset(
        Long id,
        String title,
        long viewCount,
        String createdDate
) {}
