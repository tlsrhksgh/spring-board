package com.single.springboard.domain.posts.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.posts.PostsCustomRepository;
import com.single.springboard.web.dto.posts.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.single.springboard.domain.posts.QPosts.posts;

@RequiredArgsConstructor
public class PostsCustomRepositoryImpl implements PostsCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<SearchResponse> findAllByKeyword(String keyword, Pageable pageable) {
        List<SearchResponse> content = query.select(Projections.constructor(SearchResponse.class,
                        posts.id,
                        posts.title,
                        posts.content,
                        posts.user.name,
                        formattedModifiedDate(posts.modifiedDate)
                ))
                .from(posts)
                .where(likePostTitleAndContent(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(posts.id.desc())
                .fetch();

        JPAQuery<Long> count = query.select(posts.count())
                .from(posts)
                .where(likePostTitleAndContent(keyword));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression likePostTitleAndContent(String keyword) {
        if (StringUtils.hasText(keyword)) {

            return posts.title.likeIgnoreCase("%" + keyword + "%")
                    .or(posts.content.likeIgnoreCase("%" + keyword + "%"));
        }
        return null;
    }

    private Expression<String> formattedModifiedDate(DateTimePath<LocalDateTime> date) {
        return ExpressionUtils.as(
                Expressions.stringTemplate("FORMATDATETIME({0}, {1})", date, "yyyy-MM-dd HH:mm:ss"),
                "formattedModifiedDate"
        );
    }
}
