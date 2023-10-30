package com.single.springboard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.client.RedisClient;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.post.PostRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.user.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.single.springboard.exception.ErrorCode.NOT_FOUND_POST;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostUtils {
    private final ObjectMapper objectMapper;
    private final RedisClient redisClient;
    private final PostRepository postRepository;

    public Map<Long, String> parseJsonStringToMap(String oldFileNameJson) {
        Map<Long, String> oldFileMap = new HashMap<>();
        if(oldFileNameJson == null) {
            return oldFileMap;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(oldFileNameJson);
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                String parseValue = String.valueOf(next.getValue()).replace("\"", "");
                oldFileMap.put(Long.parseLong(next.getKey()), parseValue);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oldFileMap;
    }

    @Async
    @Transactional
    public void increasePostViewCount(SessionUser user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        boolean isReadPost = redisClient.checkReadAndIncreaseView(user.getEmail(), postId.toString(), post.getTitle());

        if (!isReadPost) {
            postRepository.increaseViewCount(postId);
        }
    }
}
