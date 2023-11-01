package com.single.springboard.domain.comment;

import com.single.springboard.domain.dto.comment.CommentPaginationDto;

import java.util.List;

public interface CommentCustomRepository {

    List<CommentPaginationDto> commentListPaginationNoOffset(Long commentId, String username, int pageSize);

    Comment getComment(Long commentId);
}
