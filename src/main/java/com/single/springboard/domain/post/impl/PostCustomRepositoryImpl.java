package com.single.springboard.domain.post.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.post.PostCustomRepository;
import com.single.springboard.domain.post.dto.MainPostListNoOffset;
import com.single.springboard.domain.post.dto.PostListPaginationNoOffset;
import com.single.springboard.util.JpaCommonUtils;
import com.single.springboard.web.dto.post.SearchResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.single.springboard.domain.comment.QComment.comment;
import static com.single.springboard.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;
    private final EntityManager em;

    @Override
    public List<MainPostListNoOffset> findAllPostWithCommentsNoOffset(Long postId, int pageSize) {
        return query
                .select(Projections.constructor(MainPostListNoOffset.class,
                        post.id.as("postId"),
                        post.title,
                        post.user.name.as("author"),
                        comment.count().as("commentCount"),
                        post.viewCount,
                        JpaCommonUtils.formattedModifiedDate(post.modifiedDate)))
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
    public Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable) {
        List<SearchResponse> content = query.select(Projections.constructor(SearchResponse.class,
                        post.id,
                        post.title,
                        post.content,
                        post.user.name,
                        JpaCommonUtils.formattedModifiedDate(post.modifiedDate)
                ))
                .from(post)
                .where(likePostTitleAndContent(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        JPAQuery<Long> count = query.select(post.count())
                .from(post)
                .where(likePostTitleAndContent(keyword));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression likePostTitleAndContent(String keyword) {
        if (StringUtils.hasText(keyword)) {

            return post.title.likeIgnoreCase("%" + keyword + "%")
                    .or(post.content.likeIgnoreCase("%" + keyword + "%"));
        }
        return null;
    }

    @Override
    public List<PostListPaginationNoOffset> postListPaginationNoOffset(Long postId, String username, int pageSize) {
        return query
                .select(Projections.constructor(PostListPaginationNoOffset.class,
                        post.id.as("postId"),
                        post.title,
                        post.viewCount,
                        JpaCommonUtils.formattedModifiedDate(post.createdDate)))
                .from(post)
                .where(
                        ltPostId(postId),
                        post.user.name.eq(username)
                )
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    @Transactional
    @Override
    public void deleteAllPostByIds(List<Long> postIds) {
        String commentDelJpql = "DELETE FROM Comment c WHERE c.post.id IN :postIds";
        em.createQuery(commentDelJpql)
                .setParameter("postIds", postIds)
                .executeUpdate();

        String postDelJpql = "DELETE FROM Post p WHERE p.id IN :postIds";
        em.createQuery(postDelJpql)
                .setParameter("postIds", postIds)
                .executeUpdate();
    }

    private BooleanExpression loePostId(Long postId) {
        if(postId == null) {
            return null;
        }

        return post.id.loe(postId);
    }

    private BooleanExpression ltPostId(Long postId) {
        if(postId == null) {
            return null;
        }

        return post.id.lt(postId);
    }
}
