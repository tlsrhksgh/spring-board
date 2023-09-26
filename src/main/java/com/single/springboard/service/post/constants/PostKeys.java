package com.single.springboard.service.post.constants;

public enum PostKeys {
    RANKING("ranking"),
    USER_VIEW("post:view:user:"),
    POST_TOTAL_COUNT("post:total:count");

    private final String key;

    PostKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
