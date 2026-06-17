package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 오류가 발생했습니다."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 정보입니다."),

    // Channel Errors
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "비공개 채널은 수정할 수 없습니다."),

    // Message Errors
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 메시지를 찾을 수 없습니다."),
    INVALID_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "내용을 입력하세요."),

    // Binary Content Errors
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 파일을 찾을 수 없습니다."),

    // Read Status Errors
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 읽음 상태를 찾을 수 없습니다."),
    DUPLICATE_READ_STATUS(HttpStatus.BAD_REQUEST, "해당 유저와 채널에 대한 읽음 상태가 이미 존재합니다."),

    // Notification Errors
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),

    // JWT Errors
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패했습니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "JWT가 만료되었습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "JWT 형식이 올바르지 않습니다."),
    JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 발급에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
