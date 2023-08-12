package com.single.springboard.domain.posts.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsCustomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.single.springboard.domain.posts.QPosts.posts;

@Repository
@RequiredArgsConstructor
public class PostsCustomRepositoryImpl implements PostsCustomRepository {
    private final JPAQueryFactory query;
    private final EntityManager em;

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
