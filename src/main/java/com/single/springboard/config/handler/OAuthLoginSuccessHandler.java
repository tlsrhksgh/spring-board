package com.single.springboard.config.handler;

import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuthUser = (DefaultOAuth2User) authentication.getPrincipal();
        User findUser = userRepository.findByEmail((String) oAuthUser.getAttributes().get("email"))
                .orElseThrow(() -> new RuntimeException("로그인이 정상적으로 되지 않았습니다. 다시 시도해 주세요."));

        if(findUser.isSameName()) {
            response.sendRedirect("/user/info");
        } else {
            response.sendRedirect("/");
        }
    }
}
