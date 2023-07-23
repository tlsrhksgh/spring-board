package com.single.springboard.web.dto;


import java.time.LocalDateTime;

public record PostsListResponse(
        Long id,
        String title,
        String author,
        LocalDateTime modifiedDate
) {

}
