package com.single.springboard.domain.posts.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsCustomRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.single.springboard.domain.posts.QPosts.posts;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostsCustomRepositoryImpl implements PostsCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<Posts> findAllByKeyword(String keyword) {
        return query.select(posts)
                .from(posts)
                .where(likePostTitleAndContent(keyword))
                .orderBy(posts.id.desc())
                .fetch();
    }

    private BooleanExpression likePostTitleAndContent(String keyword) {
        if(StringUtils.hasText(keyword)) {

            return posts.title.likeIgnoreCase("%" + keyword + "%")
                    .or(posts.content.likeIgnoreCase("%" + keyword + "%"));
        }
        return null;
    }
}
