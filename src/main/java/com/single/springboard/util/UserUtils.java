package com.single.springboard.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class UserUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<String> temporaryUserIncrScript;

    public String getTemporaryUserNumber() {
        String increment = "1";
        return redisTemplate.execute(temporaryUserIncrScript,
                Collections.singletonList("temporaryUserNumber"), increment);
    }
}
