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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.single.springboard.exception.ErrorCode.POST_CHANGE_FAIL;
import static com.single.springboard.service.post.constants.PostKeys.RANKING;

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
        String redisValue = (String) redisTemplate.opsForValue().get(key);
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

    public void put(Long key, Post post) {
        put(key.toString(), post);
    }

    private void put(String key, Post post) {
        try {
            redisTemplate.opsForValue().set(key, mapper.writeValueAsString(post));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new CustomException(POST_CHANGE_FAIL);
        }
    }

    public void delete(Long key) {
        redisTemplate.delete(key.toString());
    }

    public void delete(List<Long> key) {
        redisTemplate.delete(key.stream()
                .map(Object::toString)
                .collect(Collectors.toList()
                ));
    }

    public void increasePostViewCount(String userId, Post post) {
        String postId = post.getId().toString();
        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userId, postId));

        if (!hasViewed) {
            redisTemplate.opsForZSet().incrementScore(RANKING.getKey(), post.getTitle() + ":" + postId, 1);
            redisTemplate.opsForSet().add(userId, postId);
            post.increaseViewCount();
        }
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
