package com.single.springboard.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 게시물이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원이므로 회원 가입이 필요합니다."),
    UNAUTHORIZED_USER_REQUIRED_LOGIN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    IS_EXIST_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 이름입니다. 이름을 다시 수정 해주세요."),
    EXIST_USERNAME_SET_TEMPORARY_NAME(HttpStatus.BAD_REQUEST, "회원 가입은 완료 되었으나 이미 사용중인 이름이 있습니다. 프로필 수정을 해주세요."),
    IS_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    POST_CHANGE_FAIL(HttpStatus.BAD_REQUEST, "게시글을 추가/변경할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String content;
}