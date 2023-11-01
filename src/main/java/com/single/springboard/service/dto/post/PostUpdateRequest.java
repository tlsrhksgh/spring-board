package com.single.springboard.service.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostUpdateRequest(
        @NotBlank(message = "제목은 1 ~ 500자 이여야 합니다.")
        String title,

        @NotBlank
        String author,

        @NotEmpty(message = "수정하실 게시글 내용을 입력 해주세요.")
        String content,

        List<MultipartFile> files,

        String oldFileNames
) {

}
