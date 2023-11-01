package com.single.springboard.service.dto.comment;

import com.single.springboard.domain.comment.Comment;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class CommentsResponse {
    private Long id;
    private String author;
    private String content;
    private Comment parentId;
    private int commentLevel;
    private String createdDate;
}
