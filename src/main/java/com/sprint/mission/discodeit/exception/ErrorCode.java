package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // --- User ---
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "USER_001", "이름은 2자 이상, 10자 이하로 설정하세요."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "USER_002", "이메일은 공백 없이 필수 입력 사항입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호는 공백 없이 8자 이상이어야 합니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_004", "존재하지 않은 사용자입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER_005", "이미 존재하는 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "USER_006", "이미 존재하는 이름입니다."),

    // --- Channel ---
    INVALID_CHANNEL_NAME(HttpStatus.BAD_REQUEST, "CH_001", "채널 이름은 2자 이상, 15자 이하로 설정하세요."),
    CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "CH_002", "채널을 찾을 수 없습니다."),
    ALREADY_IN_CHANNEL(HttpStatus.CONFLICT, "CH_003", "이미 채널에 참여 중인 유저입니다."),
    NOT_A_MEMBER(HttpStatus.BAD_REQUEST, "CH_004", "채널에 참여하지 않은 유저는 나갈 수 없습니다."),
    PRIVATE_CHANNEL_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "CH_005", "PRIVATE 채널은 수정할 수 없습니다."),

    // --- Message ---
    EMPTY_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "MSG_001", "메세지 내용을 입력해주세요."),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "MSG_002", "메세지는 500자 이하로 작성해주세요."),
    MESSAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MSG_003", "존재하지 않는 메세지입니다."),

    // --- Auth ---
    INVALID_PASSWORD_MATCH(HttpStatus.UNAUTHORIZED, "AUTH_001", "비밀번호가 일치하지 않습니다."),

    // --- Status ---
    USER_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "STAT_001", "유저 상태 정보를 찾을 수 없습니다."),
    READ_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "STAT_002", "이미 존재하는 읽음 상태입니다."),

    // --- System ---
    STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001", "파일 저장 또는 삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}