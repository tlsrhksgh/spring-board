package com.single.springboard.service.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UserUpdateRequest(
        @NotBlank(message = "이메일 주소는 반드시 필요합니다.")
        String email,
        String name,
        List<MultipartFile> picture
) {
}
