package com.single.springboard.client.constants;

public enum PostKeys {
    RANKING("ranking"),
    POSTS_KEY("posts");

    private final String key;

    PostKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
