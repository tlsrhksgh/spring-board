package com.single.springboard.web.dto.post;

import com.single.springboard.domain.file.File;
import lombok.Builder;

import java.util.List;

@Builder
public record PostResponse(
        Long id,
        String title,
        String author,
        String content,
        List<File> files
) {
}
