package com.single.springboard.service.post.dto;

import lombok.Builder;

@Builder
public record PostRankingResponse(
        Long id,
        String title
) {
}
