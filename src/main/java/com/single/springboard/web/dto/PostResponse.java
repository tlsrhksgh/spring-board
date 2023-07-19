package com.single.springboard.web.dto;

public record PostResponse(
        Long id,
        String author,
        String content,
        String title
) {
}
