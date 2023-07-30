package com.single.springboard.web.dto.posts;


import java.time.LocalDateTime;

public record PostsResponse(
        Long id,
        String title,
        String author,
        LocalDateTime modifiedDate
) {

}
