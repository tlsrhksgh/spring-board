package com.single.springboard.domain.comment.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.comment.CommentCustomRepository;
import com.single.springboard.domain.comment.dto.CommentPaginationDto;
import com.single.springboard.domain.common.CommonUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.single.springboard.domain.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<CommentPaginationDto> commentListPagination(Long commentId, String username, int pageSize) {
        return query
                .select(Projections.constructor(CommentPaginationDto.class,
                        comment.id.as("commentId"),
                        comment.post.id.as("postId"),
                        comment.post.title,
                        comment.content,
                        CommonUtils.formattedModifiedDate(comment.createdDate),
                        comment.children.size().as("childrenCount")))
                .from(comment)
                .where(
                        ltCommentId(commentId),
                        comment.user.name.eq(username)
                )
                .orderBy(comment.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltCommentId(Long commentId) {
        if(commentId == null) {
            return null;
        }

        return comment.id.lt(commentId);
    }
}
