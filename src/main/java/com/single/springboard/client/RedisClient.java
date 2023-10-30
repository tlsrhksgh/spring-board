package com.single.springboard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.post.dto.PostRankingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    public boolean checkReadAndIncreaseView(String userEmail, String postId, String postTitle) {
        boolean hasViewed = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userEmail, postId));

        if(!hasViewed) {
            String mergeTitle = postTitle + ":" + postId;
            redisTemplate.opsForZSet().incrementScore(RANKING.getKey(), mergeTitle, 1);
            redisTemplate.opsForSet().add(userEmail, postId);
            return false;
        }

        return true;
    }

    public List<PostRankingResponse> getPostsRanking() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                zSetOperations.reverseRangeWithScores(RANKING.getKey(), 0, 4);

        List<PostRankingResponse> rankingList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(typedTuples)) {
            List<PostRankingResponse> list = typedTuples.stream()
                    .map(tuple -> {
                        String convertToString = String.valueOf(tuple.getValue());
                        String[] splitTitle = convertToString.split(":");
                        int idLength = splitTitle[splitTitle.length - 1].length();

                        return PostRankingResponse.builder()
                                .id(Long.parseLong(splitTitle[splitTitle.length - 1]))
                                .title(convertToString.substring(0, (convertToString.length() - idLength - 1)))
                                .build();
                    })
                    .toList();

            rankingList.addAll(list);
        }

        return rankingList;
    }
}
