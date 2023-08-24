package com.single.springboard.scheduler;

import com.single.springboard.service.posts.dto.PostRankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RankingScheduler {
    private final RedisTemplate<String, String> redisTemplate;

    public List<PostRankResponse> getPostsRanking() {
        String key = "ranking";
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 4);

        List<PostRankResponse> responses = new ArrayList<>();
        if (!typedTuples.isEmpty()) {
            List<PostRankResponse> list = typedTuples.stream()
                    .map(tuple -> {
                        String[] splitValue = tuple.getValue().split(":");
                        return PostRankResponse.builder()
                                .id(Long.parseLong(splitValue[splitValue.length - 1]))
                                .title(splitValue[0])
                                .score(tuple.getScore().longValue())
                                .build();
                    })
                    .toList();

            responses.addAll(list);
        }

        return responses;
    }
}
