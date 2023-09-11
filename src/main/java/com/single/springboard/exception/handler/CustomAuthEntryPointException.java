package com.single.springboard.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.springboard.exception.ExceptionForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.single.springboard.exception.ErrorCode.UNAUTHORIZED_USER_REQUIRED_LOGIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthEntryPointException implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ExceptionForm exceptionForm = new ExceptionForm(
                UNAUTHORIZED_USER_REQUIRED_LOGIN.getContent(),
                UNAUTHORIZED_USER_REQUIRED_LOGIN.getHttpStatus().value());
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(UNAUTHORIZED_USER_REQUIRED_LOGIN.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(exceptionForm));
    }
}