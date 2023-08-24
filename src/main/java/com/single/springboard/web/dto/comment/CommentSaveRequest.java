package com.single.springboard.web.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentSaveRequest(
        @NotNull
        Long postId,
        Long parentId,

        @NotNull
        String nickname,
        boolean secret,
        @NotBlank(message = "댓글 내용은 한 글자 이상 작성해주셔야 합니다.")
        String content
) {}
