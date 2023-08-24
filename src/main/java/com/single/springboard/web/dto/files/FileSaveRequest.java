package com.single.springboard.web.dto.files;

import com.single.springboard.domain.file.File;
import com.single.springboard.domain.post.Post;
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
        public File toEntity(Post post) {
                return File.builder()
                        .originalName(originalName)
                        .translateName(translateName)
                        .size(size)
                        .post(post)
                        .createdDate(LocalDateTime.now())
                        .build();
        }
}
