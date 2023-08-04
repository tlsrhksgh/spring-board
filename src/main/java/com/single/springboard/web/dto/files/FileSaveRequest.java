package com.single.springboard.web.dto.files;

import com.single.springboard.domain.files.Files;
import com.single.springboard.domain.posts.Posts;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FileSaveRequest(
        @NotBlank(message = "한글자 이상의 파일명이 있어야합니다.")
        String originalName,
        String translateName,
        Long postId,
        long size
) {
        public Files toEntity(Posts post) {
                return Files.builder()
                        .originalName(originalName)
                        .translateName(translateName)
                        .size(size)
                        .posts(post)
                        .createdDate(LocalDateTime.now())
                        .build();
        }
}
