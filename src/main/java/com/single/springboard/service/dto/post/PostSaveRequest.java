package com.single.springboard.service.dto.post;

import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostSaveRequest(
        @NotBlank(message = "제목은 띄어쓰기만 있을 수 없습니다. 한 글자 이상 입력해 주세요.")
        String title,

        @NotEmpty(message = "게시글 내용 작성은 필수입니다. 한 글자 이상 입력해 주세요.")
        String content,

        @NotNull
        String author,
        List<MultipartFile> files
) {
        public Post toEntity(User user) {
                return Post.builder()
                        .title(title)
                        .content(content)
                        .author(author)
                        .user(user)
                        .build();
        }
}
