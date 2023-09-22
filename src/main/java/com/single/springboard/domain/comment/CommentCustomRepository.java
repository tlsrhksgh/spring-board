package com.single.springboard.domain.comment;

import com.single.springboard.domain.comment.dto.CommentPaginationDto;

import java.util.List;

public interface CommentCustomRepository {

    List<CommentPaginationDto> commentListPagination(Long commentId, String username, int pageSize);
}
