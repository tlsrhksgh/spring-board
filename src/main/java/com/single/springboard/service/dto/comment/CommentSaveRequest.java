package com.single.springboard.service.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentSaveRequest(
        @NotNull
        Long postId,
        Long parentId,

        @NotNull
        String author,
        boolean secret,
        @NotBlank(message = "댓글 내용은 한 글자 이상 작성해주셔야 합니다.")
        String content
) {}
