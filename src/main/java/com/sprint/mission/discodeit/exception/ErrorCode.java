package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다."),
    ALREADY_EXISTS_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 이름입니다."),
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"Private Channel은 수정할 수 없습니다."),
    CHANNEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 Channel 입니다."),
    NOT_PRIVATE_CHANNEL_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST, "Private Channel의 가입되지 않은 사용자입니다."),
    /// Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 메시지입니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 파일입니다."),

    // Auth
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다."),

    // ReadStatus
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 ReadStatus입니다."),

    // UserStatus
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 UserStatus"),

    // Storage
    FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."),
    DUPLICATE_FILE(HttpStatus.CONFLICT, "이미 존재하는 파일입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
