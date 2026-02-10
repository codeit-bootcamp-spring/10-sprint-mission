package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 바디(JSON)가 올바르지 않습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다."),
    MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "메시지 내용이 필요합니다."),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호는 빈 값이 될 수 없습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "상태 정보를 찾을 수 없습니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),

    // 409
    DUPLICATION_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    DUPLICATION_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATION_CHANNEL(HttpStatus.CONFLICT, "이미 존재하는 채널입니다."),
    DUPLICATION_READ_STATUS(HttpStatus.CONFLICT, "이미 존재하는 수신 정보입니다."),
    ALREADY_JOINED_CHANNEL(HttpStatus.CONFLICT, "이미 채널에 참여한 사용자입니다."),
    USER_NOT_IN_CHANNEL(HttpStatus.CONFLICT, "사용자가 채널에 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 현재 상태와 충돌합니다."),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
