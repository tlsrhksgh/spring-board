package com.single.springboard.domain.post.dto;

import java.time.LocalDateTime;

public record PostPaginationDto(
        Long id,
        String title,
        LocalDateTime createdDate
) {}
