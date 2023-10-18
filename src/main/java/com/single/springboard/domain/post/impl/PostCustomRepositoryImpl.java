package com.single.springboard.domain.post.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.DateFormatUtils;
import com.single.springboard.domain.post.PostCustomRepository;
import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.single.springboard.domain.comment.QComment.comment;
import static com.single.springboard.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<MainPostListNoOffset> findAllPostWithCommentsNoOffset(Long postId, int pageSize) {
        return query
                .select(Projections.constructor(MainPostListNoOffset.class,
                        post.id.as("postId"),
                        post.title,
                        post.user.name.as("author"),
                        comment.count().as("commentCount"),
                        post.viewCount,
                        DateFormatUtils.formatDateTime(post.modifiedDate)
                ))
                .from(post)
                .leftJoin(comment)
                .on(comment.post.id.eq(post.id))
                .where(loePostId(postId))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize) {
        return query
                .select(Projections.constructor(PostListPaginationNoOffset.class,
                        post.id.as("postId"),
                        post.title,
                        post.viewCount,
                        DateFormatUtils.formatDateTime(post.modifiedDate)
                ))
                .from(post)
                .where(
                        ltPostId(postId),
                        post.user.name.eq(username)
                )
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public void deleteAllPostByIds(List<Long> postIds, String username) {
        query.delete(post)
                .where(
                        post.id.in(postIds),
                        post.user.name.eq(username))
                .execute();
    }

    private BooleanExpression loePostId(Long postId) {
        if (postId == null) {
            return null;
        }

        return post.id.loe(postId);
    }

    private BooleanExpression ltPostId(Long postId) {
        if (postId == null) {
            return null;
        }

        return post.id.lt(postId);
    }
}
