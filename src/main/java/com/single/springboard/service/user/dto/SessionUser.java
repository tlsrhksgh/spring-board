package com.single.springboard.service.user.dto;

import com.single.springboard.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private final String name;
    private final String email;
    private final String picture;
    private final boolean isSameName;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        this.isSameName = user.isSameName();
    }
}
