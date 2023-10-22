package com.single.springboard.service.post.constants;

public enum PostKeys {
    RANKING("ranking");

    private final String key;

    PostKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
