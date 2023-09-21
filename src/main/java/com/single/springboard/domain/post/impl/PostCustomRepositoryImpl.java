package com.single.springboard.domain.post.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.post.PostCustomRepository;
import com.single.springboard.domain.post.dto.PostPaginationDto;
import com.single.springboard.web.dto.post.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.single.springboard.domain.post.QPost.post;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable) {
        List<SearchResponse> content = query.select(Projections.constructor(SearchResponse.class,
                        post.id,
                        post.title,
                        post.content,
                        post.user.name,
                        formattedModifiedDate(post.modifiedDate)
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

    private Expression<String> formattedModifiedDate(DateTimePath<LocalDateTime> date) {
        return ExpressionUtils.as(
                Expressions.stringTemplate("FORMATDATETIME({0}, {1})", date, "yyyy-MM-dd HH:mm:ss"),
                "formattedModifiedDate"
        );
    }

    @Override
    public List<PostPaginationDto> postPagination(Long postId, String username, int pageSize) {
        return query
                .select(Projections.constructor(PostPaginationDto.class,
                        post.id.as("postId"),
                        post.title,
                        post.createdDate))
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
}
