package com.single.springboard.config.auth.dto;

import com.single.springboard.domain.user.User;

import java.io.Serializable;

public record SessionUser (
        String name,
        String email,
        String picture
) implements Serializable {
    public SessionUser(User user) {
        this(user.getName(), user.getEmail(), user.getPicture());
    }
}
