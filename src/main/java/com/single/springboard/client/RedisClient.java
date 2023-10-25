package com.single.springboard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.domain.post.Post;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.post.dto.PostRankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.single.springboard.client.constants.PostKeys.POSTS_KEY;
import static com.single.springboard.client.constants.PostKeys.RANKING;
import static com.single.springboard.exception.ErrorCode.POST_CHANGE_FAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;

    public <T> T get(Long key, Class<T> classType) {
        return get(key.toString(), classType);
    }

    private <T> T get(String key, Class<T> classType) {
        String redisValue = (String) redisTemplate.opsForHash().get(POSTS_KEY.getKey(), key);
        if (ObjectUtils.isEmpty(redisValue)) {
            return null;
        } else {
            try {
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.error("Parsing Error", e);
                return null;
            }
        }
    }

    public void put(Long key, Object obj) {
        put(key.toString(), obj);
    }

    private void put(String key, Object obj) {
        try {
            redisTemplate.opsForHash().put(POSTS_KEY.getKey(), key, mapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new CustomException(POST_CHANGE_FAIL);
        }
    }

    public void delete(String key, List<Long> hashKeys) {
        Object[] keysArr = Arrays.stream(hashKeys.toArray())
                        .map(Object::toString)
                        .toArray();
        redisTemplate.opsForHash().delete(key, keysArr);
    }

    public Long checkReadAndIncreaseView(String userId, Post post) {
        String postId = post.getId().toString();
        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userId, postId));

        if(!hasViewed) {
            Double score = redisTemplate.opsForZSet().incrementScore(
                    RANKING.getKey(), post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userId, postId);
            return Objects.nonNull(score) ? score.longValue() : null;
        }

        return null;
    }

    public List<PostRankingResponse> getPostsRanking() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                zSetOperations.reverseRangeWithScores(RANKING.getKey(), 0, 4);

        List<PostRankingResponse> responses = new ArrayList<>();
        if (typedTuples != null) {
            List<PostRankingResponse> list = typedTuples.stream()
                    .map(tuple -> {
                        String[] splitValue = String.valueOf(tuple.getValue()).split(":");
                        return PostRankingResponse.builder()
                                .id(Long.parseLong(splitValue[splitValue.length - 1]))
                                .title(splitValue[0])
                                .build();
                    })
                    .toList();

            responses.addAll(list);
        }

        return responses;
    }
}
