package com.single.springboard.service.user.dto.oauth;

import com.single.springboard.service.user.dto.OAuthAttributes;

import java.util.Map;

public interface OAuthInterface {
    OAuthAttributes create(String userNameAttributeName, Map<String, Object> attributes);
}
