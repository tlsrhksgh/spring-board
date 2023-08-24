package com.single.springboard.service.post.dto;

import lombok.Builder;

@Builder
public record PostRankResponse(
        Long id,
        String title,
        long score
) {
}
