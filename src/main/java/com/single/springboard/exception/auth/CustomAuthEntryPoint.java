package com.single.springboard.exception.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.single.springboard.exception.ErrorCode.UNAUTHORIZED_USER;
import static org.springframework.http.MediaType.*;

@Configuration
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(UNAUTHORIZED_USER.getHttpStatus().value());
        response.getWriter().write(UNAUTHORIZED_USER.getContent());
    }
}
