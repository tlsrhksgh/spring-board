package com.single.springboard.domain.comment.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.DateFormatUtils;
import com.single.springboard.domain.comment.Comment;
import com.single.springboard.domain.comment.CommentCustomRepository;
import com.single.springboard.domain.comment.dto.CommentPaginationDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.single.springboard.domain.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<CommentPaginationDto> commentListPaginationNoOffset(Long commentId, String username, int pageSize) {
        return query
                .select(Projections.constructor(CommentPaginationDto.class,
                        comment.id.as("commentId"),
                        comment.post.id.as("postId"),
                        comment.post.title,
                        comment.content,
                        DateFormatUtils.formatDateTime(comment.createdDate),
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

    @Override
    public Comment getComment(Long commentId) {
        return query
                .select(comment)
                .from(comment)
                .leftJoin(comment.post)
                .fetchJoin()
                .leftJoin(comment.user)
                .fetchJoin()
                .leftJoin(comment.children)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();
    }

    private BooleanExpression ltCommentId(Long commentId) {
        if(commentId == null) {
            return null;
        }

        return comment.id.lt(commentId);
    }
}
