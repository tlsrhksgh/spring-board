package com.single.springboard.service.user.dto.oauth.OAuthInterfaceImpl;

import com.single.springboard.service.user.dto.OAuthAttributes;
import com.single.springboard.service.user.dto.oauth.OAuthInterface;

import java.util.Map;

public class NaverOAuthAttribute implements OAuthInterface {
    @Override
    public OAuthAttributes create(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>)attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
