package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User (U)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "U003", "이미 존재하는 사용자 이름입니다."),

    // Channel (C)
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "해당 채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "C002", "프라이빗 채널은 수정할 수 없습니다."),

    // Message (M)
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "해당 메시지를 찾을 수 없습니다."),

    // Global (G)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
