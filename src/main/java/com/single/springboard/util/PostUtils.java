package com.single.springboard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PostUtils {
    private final ObjectMapper objectMapper;

    public Map<Long, String> parseJsonStringToMap(String oldFileNameJson) {
        Map<Long, String> oldFileMap = new HashMap<>();

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
}
