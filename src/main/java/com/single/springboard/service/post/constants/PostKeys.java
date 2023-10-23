package com.single.springboard.service.post.constants;

public enum PostKeys {
    RANKING("ranking"),
    POST_ID_KEY("post:post_id:");

    private final String key;

    PostKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
