package com.sprint.mission.discodeit.exception.security.handler;

import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.security.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(
            InvalidRefreshTokenException e
    ) {
        log.warn("[AUTH_UNAUTHENTICATED] 인증되지 않은 요청: timestamp={}, code={}, message={}, details={}",
                e.getTimestamp(), e.getErrorCode().name(), e.getMessage(), e.getDetails());

        ResponseCookie invalidRefreshTokenCookie = ResponseCookie
                .from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false) // local용
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ErrorResponse errorResponse = new ErrorResponse(
                e,
                HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, invalidRefreshTokenCookie.toString())
                .body(errorResponse);
    }
}
