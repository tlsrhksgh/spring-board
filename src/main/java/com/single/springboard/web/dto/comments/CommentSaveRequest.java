package com.single.springboard.web.dto.comments;

import jakarta.validation.constraints.NotBlank;

public record CommentSaveRequest(
        Long postId,
        Long parentId,
        String nickname,
        boolean secret,
        @NotBlank(message = "댓글 내용은 한 글자 이상 작성해주셔야 합니다.")
        String content
) {}
