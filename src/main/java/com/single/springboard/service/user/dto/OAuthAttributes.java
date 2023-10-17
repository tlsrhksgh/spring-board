package com.single.springboard.service.user.dto;

import com.single.springboard.domain.user.Role;
import com.single.springboard.domain.user.User;
import com.single.springboard.service.user.dto.oauth.OAuthAttributesFactory;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return OAuthAttributesFactory.valueOf(registrationId.toUpperCase())
                .getType()
                .get()
                .create("id", attributes);
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .build();
    }
}
