package com.single.springboard.service.posts;

import com.single.springboard.domain.posts.Posts;
import com.single.springboard.domain.posts.PostsRepository;
import com.single.springboard.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Component
@RequiredArgsConstructor
public class PostsUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostsRepository postsRepository;

    private static final String REDIS_POST_VIEW_KEY_PREFIX = "post:view:postId:";
    private static final String REDIS_POST_VIEW_USER_KEY_PREFIX = "post:view:user:";

    public void increasePostViewCount(String postId, String userId) {
        String postViewKey = REDIS_POST_VIEW_KEY_PREFIX + postId;
        String userViewKey = REDIS_POST_VIEW_USER_KEY_PREFIX + userId;

        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userViewKey, postId));

        if(!hasViewed) {
            Posts post = postsRepository.findById(Long.valueOf(postId))
                    .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

            redisTemplate.opsForHash().increment(postViewKey, "viewCount", 1L);
            redisTemplate.opsForSet().add(userViewKey, postId);
            post.updateViewCount();
        }
    }
}
