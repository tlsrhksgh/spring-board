package com.single.springboard.web.dto.posts;

import com.single.springboard.domain.posts.Posts;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostSaveRequest(
        @NotBlank(message = "제목 입력은 필수입니다.")
        @Size(min = 1, max = 500, message = "제목은 1 ~ 500자 이여야 합니다.")
        String title,
        @NotEmpty(message = "게시글 내용 작성은 필수입니다.")
        String content,
        String author,
        List<MultipartFile> files
) {
    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
