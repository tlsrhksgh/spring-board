package com.single.springboard.domain.post.dto;

public record PostListPaginationDto(
        Long id,
        String title,
        long viewCount,
        String createdDate
) {}
