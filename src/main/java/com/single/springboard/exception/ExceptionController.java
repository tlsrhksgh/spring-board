package com.single.springboard.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.core.AuthenticationException;

import static com.single.springboard.exception.ErrorCode.*;

@Slf4j
@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionForm> requestException(final CustomException exception) {
        log.warn("api exception: {}", exception.getErrorCode());
        return ResponseEntity.badRequest().body(
                new ExceptionForm(exception.getMessage(), exception.getErrorCode().getHttpStatus().value())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionForm> methodArgumentNotValid(final MethodArgumentNotValidException ex) {
        log.warn("validation argument field exception: {}", ex.getMessage());
        FieldError error = ex.getBindingResult().getFieldErrors().get(0);

        return ResponseEntity.badRequest()
                .body(new ExceptionForm(error.getDefaultMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionForm> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("constraint violation exception: {}", ex.getConstraintViolations());
        return ResponseEntity.badRequest().body(
                new ExceptionForm(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionForm> handleAuthenticationException(AuthenticationException ex) {
        log.warn("authentication exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionForm(USER_REQUIRED_LOGIN.getContent(), HttpStatus.UNAUTHORIZED.value()));
    }
}
