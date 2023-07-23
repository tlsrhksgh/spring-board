package com.single.springboard.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 게시물이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원이므로 회원 가입이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String content;
}