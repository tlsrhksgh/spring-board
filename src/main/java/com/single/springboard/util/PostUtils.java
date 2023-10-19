package com.single.springboard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.domain.post.Post;
import com.single.springboard.domain.user.User;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.user.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.single.springboard.exception.ErrorCode.IS_WRONG_ACCESS;

@RequiredArgsConstructor
@Component
public class PostUtils {
    private final ObjectMapper objectMapper;

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

    public void checkPostAuthor(Post post, SessionUser user) {
        User author = post.getUser();
        if(!Objects.equals(author.getName(), user.getName()) || !Objects.equals(author.getEmail(), user.getEmail())) {
            throw new CustomException(IS_WRONG_ACCESS);
        }
    }
}
