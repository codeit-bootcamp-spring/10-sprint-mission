package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType; // 발생한 예외의 클래스 이름
    private int status; // HTTP 상태코드

    public ErrorResponse(DiscodeitException e, int status) {
        this.timestamp = e.getTimestamp();
        this.code = e.getErrorCode().name();
        this.message = e.getMessage();
        this.details = e.getDetails();
        this.exceptionType = e.getClass().getSimpleName(); // 예외 발생 파일 이름
        this.status = status;
    }

    public ErrorResponse(Exception e, int status) {
        this.timestamp = Instant.now();
        this.code = e.getClass().getSimpleName(); // 예외 발생 파일 이름
        this.message = e.getMessage();
        this.details = new HashMap<>();
        this.exceptionType = e.getClass().getSimpleName();
        this.status = status;
    }

    // 로그인 실패 전용 정적 팩토리 메서드
    public static ErrorResponse loginFailure(AuthenticationException e, int status) {
        return new ErrorResponse(
                Instant.now(),
                "LOGIN_FAILED",
                "아이디 또는 비밀번호가 올바르지 않습니다.",
                new HashMap<>(),
                e.getClass().getSimpleName(),
                status
        );
    }

    // 인증이 필요한 API에 인증되지 않은 사용자의 접근 차단 전용 정적 팩토리 메서드
    public static ErrorResponse unauthenticated(AuthenticationException e, int status) {
        return new ErrorResponse(
                Instant.now(),
                "AUTH_UNAUTHENTICATED",
                "인증되지 않은 사용자는 접근할 수 없습니다.",
                new HashMap<>(),
                e.getClass().getSimpleName(),
                status
        );
    }

    // 권한이 부족한 사용자의 접근 차단 전용 정적 팩토리 메서드
    public static ErrorResponse accessDenied(AccessDeniedException e, int status) {
        return new ErrorResponse(
                Instant.now(),
                "AUTH_ACCESS_DENIED",
                "접근 권한이 없습니다.",
                new HashMap<>(),
                e.getClass().getSimpleName(),
                status
        );
    }
}
