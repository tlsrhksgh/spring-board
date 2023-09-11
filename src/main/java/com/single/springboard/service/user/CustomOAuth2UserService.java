package com.single.springboard.service.user;

import com.single.springboard.domain.user.User;
import com.single.springboard.domain.user.UserRepository;
import com.single.springboard.exception.CustomException;
import com.single.springboard.service.file.FileService;
import com.single.springboard.service.user.dto.OAuthAttributes;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.user.UserUpdateRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static com.single.springboard.exception.ErrorCode.IS_EXIST_USERNAME;
import static com.single.springboard.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<String> temporaryUserIncrScript;

    private static final String UPLOAD_URL = "https://spring-board-file.s3.ap-northeast-2.amazonaws.com/";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 위임받은 사이트 식별 id
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, oAuth2User.getAttributes());

        User user = saveOrLoadUser(attributes);

        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrLoadUser(OAuthAttributes attributes) {
        Optional<User> user = userRepository.findByEmail(attributes.getEmail());

        if(user.isEmpty()) {
            boolean isExistName = userRepository.existsByName(attributes.getName());

            user = Optional.of(attributes.toEntity());

            if(isExistName) {
                user.get().update("사용자" + getTemporaryUserNumber());
                user.get().setSameName();
            }
        }

        return userRepository.save(user.get());
    }

    private String getTemporaryUserNumber() {
        String increment = "1";
        return redisTemplate.execute(temporaryUserIncrScript,
                Collections.singletonList("temporaryUserNumber"), increment);
    }

    @Transactional
    public void updateUser(UserUpdateRequest requestDto, SessionUser currentUser) {
        if(requestDto.name().equals(currentUser.getName())) {
            // 이름은 동일하고 이미지 파일만 변경하는 경우
            userNameAndImageUpdate(requestDto);
        } else {
            // 이름 or 이미지 파일을 변경하는 경우
            if(existUsernameCheck(requestDto.name())) {
                throw new CustomException(IS_EXIST_USERNAME);
            }
            userNameAndImageUpdate(requestDto);
        }
    }

    private boolean existUsernameCheck(String name) {
        return userRepository.existsByName(name);
    }

    private void userNameAndImageUpdate(UserUpdateRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if(requestDto.picture() != null && requestDto.picture().size() > 0) {
            String translateFileName = fileService.profileImageUpdate(requestDto.picture());
            String imageUrl = UPLOAD_URL + translateFileName;
            user.update(requestDto.name(), imageUrl);
        } else {
            user.update(requestDto.name());
        }

        httpSession.setAttribute("user", new SessionUser(user));
    }
}
