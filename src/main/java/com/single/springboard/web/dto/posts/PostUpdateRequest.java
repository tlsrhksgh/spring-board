package com.single.springboard.web.dto.posts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PostUpdateRequest(
        @Size(min = 1, max = 500, message = "제목은 1 ~ 500자 이여야 합니다.")
        String title,

        @NotEmpty(message = "수정하실 게시글 내용을 입력 해주세요.")
        String content
) {

}
