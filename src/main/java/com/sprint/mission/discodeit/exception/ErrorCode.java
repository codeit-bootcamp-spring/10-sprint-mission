package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    //사용자 관련
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME("USER_002", "이미 존재하는 사용자 이름입니다."),
    DUPLICATE_EMAIL("USER_003", "이미 존재하는 이메일입니다."),

    // 권한 관련
    INVALID_CREDENTIALS("AUTH_001", "잘못된 인증 정보입니다."),
    WRONG_PASSWORD("AUTH_002", "비밀번호가 일치하지 않습니다."),

    // 사용자 상태 관련
    USER_STATUS_NOT_FOUND("STATUS_001", "사용자 상태를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS("STATUS_002", "사용자 상태가 이미 존재합니다"),

    // 일반 오류
    INVALID_INPUT("COMMON_001", "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR("COMMON_002", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }
}
