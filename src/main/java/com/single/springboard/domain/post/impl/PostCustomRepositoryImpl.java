package com.single.springboard.domain.post.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.DateFormatUtils;
import com.single.springboard.domain.dto.post.MainPostList;
import com.single.springboard.domain.dto.post.PostListPaginationNoOffset;
import com.single.springboard.domain.post.PostCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.single.springboard.domain.comment.QComment.comment;
import static com.single.springboard.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<MainPostList> findAllPosts(Pageable pageable) {
        List<MainPostList> posts = query
                .select(Projections.constructor(MainPostList.class,
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
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = query
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(posts, pageable, totalCount);
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
        query.delete(comment)
                .where(comment.post.id.in(postIds))
                .execute();

        query.delete(post)
                .where(
                        post.id.in(postIds),
                        post.author.eq(username))
                .execute();
    }

    @Modifying
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Override
    public void increaseViewCount(Long postId) {
        query.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();
    }

    private BooleanExpression ltPostId(Long postId) {
        if (postId == null) {
            return null;
        }

        return post.id.lt(postId);
    }
}