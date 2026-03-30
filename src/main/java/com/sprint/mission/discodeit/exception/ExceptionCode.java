package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    USER_INFO_DUPLICATED(HttpStatus.CONFLICT, "User with email %s or username %s already exists"),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id %s not found"),
    USER_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "User with username %s not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id %s not found"),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel with id %s not found"),
    PRIVATE_CHANNEL_MODIFY_EXCEPTION(HttpStatus.BAD_REQUEST, "Private channel cannot be updated"),
    NOT_INVITED_IN_CHANNEL(HttpStatus.BAD_REQUEST, "채널에 속해있지 않아 메시지를 보낼 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message with id %s not found"),
    DUPLICATED_READ_STATUS(HttpStatus.BAD_REQUEST,
        "ReadStatus with userId %s and channelId %s already exists"),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "Wrong password"),
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "Read status with id %s not found"),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "UserStatus with userId %S not found"),
    USER_STATUS_WITH_USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "UserStatus with id %s not found"),
    USER_STATUS_DUPLICATED(HttpStatus.CONFLICT, "관련된 UserStatus 객체가 이미 존재합니다."),
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BinaryContent with id %s not found"),
    ATTACHMENT_SAVE_EXCEPTION(HttpStatus.BAD_REQUEST, "첨부파일을 저장할 수 없습니다."),
    BINARY_CONTENT_LOAD_EXCEPTION(HttpStatus.BAD_REQUEST, "첨부파일을 불러올 수 없습니다."),
    STORAGE_PATH_INIT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 초기화 작업에 실패하였습니다.");


    private HttpStatus statusCode;
    private String message;

    ExceptionCode(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}

