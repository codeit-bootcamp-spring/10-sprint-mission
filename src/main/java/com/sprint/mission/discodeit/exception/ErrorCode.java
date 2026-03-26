package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // User 파트
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_USER_EMAIL", "이미 사용 중인 이메일입니다."),
    DUPLICATE_USER_USERNAME(HttpStatus.BAD_REQUEST, "DUPLICATE_USER_USERNAME", "이미 사용 중인 사용자명입니다."),
    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CHANNEL_NOT_FOUND", "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "PRIVATE_CHANNEL_UPDATE", "비공개 채널은 수정할 수 없습니다."),
    // Binary
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BINARY_CONTENT_NOT_FOUND", "파일을 찾을 수 없습니다."),
    INVALID_BINARY_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_BINARY_CONTENT", "잘못된 파일 요청입니다."),
    // Message, ReadStatus, UserStatus
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_NOT_FOUND", "메시지를 찾을 수 없습니다."),
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "READ_STATUS_NOT_FOUND", "읽음 상태를 찾을 수 없습니다."),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_STATUS_NOT_FOUND", "사용자 상태를 찾을 수 없습니다."),

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
