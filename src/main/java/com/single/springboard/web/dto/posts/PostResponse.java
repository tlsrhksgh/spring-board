package com.single.springboard.web.dto.posts;

import com.single.springboard.domain.files.Files;
import lombok.Builder;

import java.util.List;

@Builder
public record PostResponse(
        Long id,
        String title,
        String author,
        String content,
        List<Files> files
) {
}
