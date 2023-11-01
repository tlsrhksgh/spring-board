package com.single.springboard.web;

import com.single.springboard.service.user.CustomOAuth2UserService;
import com.single.springboard.service.user.LoginUser;
import com.single.springboard.service.user.dto.SessionUser;
import com.single.springboard.service.dto.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserApiController {
    private final CustomOAuth2UserService oAuth2UserService;

    @PatchMapping
    public ResponseEntity<Void> userInfoUpdate(@ModelAttribute UserUpdateRequest requestDto,
                                               @LoginUser SessionUser user
    ) {
        oAuth2UserService.updateUser(requestDto, user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
