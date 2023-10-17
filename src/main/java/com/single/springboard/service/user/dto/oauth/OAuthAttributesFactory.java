package com.single.springboard.service.user.dto.oauth;

import com.single.springboard.service.user.dto.oauth.OAuthInterfaceImpl.KakaoOAuthAttribute;
import com.single.springboard.service.user.dto.oauth.OAuthInterfaceImpl.NaverOAuthAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum OAuthAttributesFactory {
    KAKAO(KakaoOAuthAttribute::new),
    NAVER(NaverOAuthAttribute::new);

    private final Supplier<OAuthInterface> type;
}
