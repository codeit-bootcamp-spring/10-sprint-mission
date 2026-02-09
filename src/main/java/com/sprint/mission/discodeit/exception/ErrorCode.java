package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Global (0~99)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G001", "잘못된 요청입니다."),
    REQUIRED_PARAMETER_MISSING(HttpStatus.BAD_REQUEST, "G002", "필수 입력 값이 누락되었습니다."),
    PATH_ID_MISMATCH(HttpStatus.BAD_REQUEST, "G003", "요청 경로의 ID와 데이터의 ID가 일치하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G004", "서버 내부 오류가 발생했습니다."),

    // System (100~199)
    DIRECTORY_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S101", "디렉토리 생성에 실패했습니다."),
    FILE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S102", "파일 저장 중 오류가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S103", "파일 삭제 중 오류가 발생했습니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "S104", "첨부된 파일 정보를 찾을 수 없습니다."),

    // Auth (200~299)
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A201", "아이디 또는 비밀번호가 일치하지 않습니다."),

    // User (300~399)
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "U301", "이름은 공백 없이, 2~10자 사이여야 합니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "U302", "이메일은 공백 없이 필수 입력 사항입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U303", "비밀번호는 공백 없이 8자 이상이어야 합니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U304", "존재하지 않은 사용자입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "U305", "이미 존재하는 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "U306", "이미 존재하는 이름입니다."),

    // Channel (400~499)
    INVALID_CHANNEL_NAME(HttpStatus.BAD_REQUEST, "C401", "채널 이름은 공백없이, 2~15자 사이여야 합니다."),
    CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "C402", "채널을 찾을 수 없습니다."),
    ALREADY_IN_CHANNEL(HttpStatus.CONFLICT, "C403", "이미 채널에 참여 중인 유저입니다."),
    NOT_A_MEMBER(HttpStatus.BAD_REQUEST, "C404", "채널 멤버가 아니면 요청을 수행할 수 없습니다."),
    PRIVATE_CHANNEL_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "C405", "비공개 채널은 수정할 수 없습니다."),

    // Message (500~599)
    EMPTY_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "M501", "메세지 내용을 입력해주세요."),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "M502", "메세지는 500자 이하로 작성해주세요."),
    MESSAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "M503", "존재하지 않는 메세지입니다."),

    // Status (600~699)
    USER_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "STAT601", "유저 상태 정보를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "STAT602", "해당 유저의 상태 정보가 이미 존재합니다."),
    READ_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "STAT603", "이미 존재하는 읽음 상태입니다."),
    READ_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "STAT604", "읽음 상태 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}