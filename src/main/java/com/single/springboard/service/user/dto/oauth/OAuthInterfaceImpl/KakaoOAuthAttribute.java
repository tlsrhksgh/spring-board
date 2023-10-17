package com.single.springboard.service.user.dto.oauth.OAuthInterfaceImpl;

import com.single.springboard.service.user.dto.OAuthAttributes;
import com.single.springboard.service.user.dto.oauth.OAuthInterface;

import java.util.Map;

public class KakaoOAuthAttribute implements OAuthInterface {
    @Override
    public OAuthAttributes create(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
