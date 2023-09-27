package com.single.springboard.domain.post.dto;

public record PostListPaginationNoOffset(
        Long id,
        String title,
        long viewCount,
        String createdDate
) {}
