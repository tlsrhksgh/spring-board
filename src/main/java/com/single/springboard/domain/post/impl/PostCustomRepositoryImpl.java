package com.single.springboard.domain.post.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.common.CommonUtils;
import com.single.springboard.domain.post.PostCustomRepository;
import com.single.springboard.domain.post.dto.PostListPaginationDto;
import com.single.springboard.web.dto.post.PostsResponse;
import com.single.springboard.web.dto.post.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.single.springboard.domain.post.QPost.post;
import static com.single.springboard.domain.comment.QComment.comment;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<PostsResponse> findAllPostWithCommentsNoOffset(Long postId, int pageSize, boolean isLessThen) {
        return query
                .select(Projections.constructor(PostsResponse.class,
                        post.id.as("postId"),
                        post.title,
                        post.user.name.as("author"),
                        comment.count().as("commentCount"),
                        post.viewCount,
                        CommonUtils.formattedModifiedDate(post.modifiedDate)))
                .from(post)
                .leftJoin(comment)
                .on(comment.post.id.eq(post.id))
                .where(isLessThen ? ltPostId(postId) : gtPostId(postId))
                .groupBy(post.id)
                .orderBy(isLessThen ? post.id.desc() : post.id.asc())
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
                        CommonUtils.formattedModifiedDate(post.modifiedDate)
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
    public List<PostListPaginationDto> postListPaginationNoOffset(Long postId, String username, int pageSize) {
        return query
                .select(Projections.constructor(PostListPaginationDto.class,
                        post.id.as("postId"),
                        post.title,
                        post.viewCount,
                        CommonUtils.formattedModifiedDate(post.createdDate)))
                .from(post)
                .where(
                        ltPostId(postId),
                        post.user.name.eq(username)
                )
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltPostId(Long postId) {
        if(postId == null) {
            return null;
        }

        return post.id.lt(postId);
    }

    private BooleanExpression gtPostId(Long postId) {
        if(postId == null) {
            return null;
        }

        return post.id.gt(postId);
    }
}
