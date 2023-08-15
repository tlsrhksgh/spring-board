package com.single.springboard.service.posts.dto;

import lombok.Builder;

@Builder
public record PostRankResponse(
        Long id,
        String title,
        long score
) {
}
