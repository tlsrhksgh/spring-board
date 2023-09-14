package com.single.springboard.service.file.dto;

import lombok.Builder;

@Builder
public record FilesNameDto(
        Long id,
        String originalName
) {
}
