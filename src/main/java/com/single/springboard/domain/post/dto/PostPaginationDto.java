package com.single.springboard.domain.post.dto;

public record PostPaginationDto(
        Long id,
        String title,
        long viewCount,
        String createdDate
) {}
