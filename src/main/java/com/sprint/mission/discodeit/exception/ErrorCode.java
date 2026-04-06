package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "사용자명 또는 비밀번호가 올바르지 않습니다."),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND,"채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "비공개 채널은 수정할 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "바이너리 콘텐츠를 찾을 수 없습니다."),
    DUPLICATE_BINARY_CONTENT(HttpStatus.BAD_REQUEST, "이미 존재하는 바이너리 콘텐츠입니다."),
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없습니다."),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 상태를 찾을 수 없습니다."),
    DUPLICATE_USER_STATUS(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 상태입니다.");

    private final String message;
    private final HttpStatus status;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
