package com.single.springboard.web;

import com.single.springboard.service.user.CustomOAuth2UserService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.web.dto.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserApiController {

    private final CustomOAuth2UserService oAuth2UserService;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @PatchMapping
    public ResponseEntity<Void> userInfoUpdate(@ModelAttribute UserUpdateRequest requestDto,
                                               @LoginUser SessionUser user
    ) {
        oAuth2UserService.updateUser(requestDto, user);

        return ResponseEntity.ok().build();
    }
}
