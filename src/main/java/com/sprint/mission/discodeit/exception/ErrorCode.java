package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    REQUEST_NOT_VALID(HttpStatus.BAD_REQUEST, "요청이 유효하지 않습니다."),
    FIELD_NOT_VALID(HttpStatus.BAD_REQUEST, "필드 값이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NAME_DUPLICATE(HttpStatus.CONFLICT, "이미 사용 중인 이름입니다."),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    CHANNEL_NAME_DUPLICATE(HttpStatus.CONFLICT, "이미 사용 중인 채널명입니다."),
    CHANNEL_CREATE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "서버 생성 요청이 유효하지 않습니다."),
    CHANNEL_PRIVATE_CREATE_EMPTY_USERS(HttpStatus.BAD_REQUEST,
        "사설 채널 생성 시에는 한 명 이상의 유저를 선택해야 합니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.FORBIDDEN, "사설 채널을 업데이트 할 수 없습니다."),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "User Status를 찾을 수 없습니다."),
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "Read Status를 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "첨부 파일을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 내부 문제입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
