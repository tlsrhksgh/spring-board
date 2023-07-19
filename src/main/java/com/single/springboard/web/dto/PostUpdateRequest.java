package com.single.springboard.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record PostUpdateRequest(
        @NotBlank
        String title,

        @NotEmpty
        String content
) {

}
