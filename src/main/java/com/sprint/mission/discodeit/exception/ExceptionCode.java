package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    //AUTH
    INVALID_CREDENTIALS(404, "INVALID_CREDENTIALS","잘못된 아이디 또는 비밀번호"),
    //USER
    USER_NOT_FOUND(404,"USER_NOT_FOUND", "존재하지 않는 사용자"),
    EMAIL_ALREADY_EXIST(409, "EMAIL_ALREADY_EXIST","이미 존재하는 이메일"),
    USER_NAME_ALREADY_EXIST(409, "USER_NAME_ALREADY_EXIST", "이미 존재하는 사용자이름"),
    //USER_STATUS
    USER_STATUS_NOT_FOUND(404, "USER_STATUS_NOT_FOUND","존재하지 않는 사용자 상태 정보"),
    USER_STATUS_ALREADY_EXIST(409,"USER_STATUS_ALREADY_EXIST","이미 존재하는 사용자 상태"),
    //CHANNEL
    CHANNEL_NOT_FOUND(404, "CHANNEL_NOT_FOUND","존재하지 않는 채널"),
    CHANNEL_ALREADY_EXIST(409,"CHANNEL_ALREADY_EXIST", "이미 존재하는 채널"),
    NOT_ALLOWED_IN_PRIVATE_CHANNEL(403,"NOT_ALLOWED_IN_PRIVATE_CHANNEL","비공개 채널에 허용되지 않은 기능"),
    //READ_STATUS
    READ_STATUS_NOT_FOUND(404, "READ_STATUS_NOT_FOUND","존재하지 않는 읽기 상태(멤버) 정보"),
    READ_STATUS_ALREADY_EXIST(409,"READ_STATUS_ALREADY_EXIST","이미 존재하는 읽기 상태(멤버)"),
    //MESSAGE
    MESSAGE_NOT_FOUND(404, "MESSAGE_NOT_FOUND","존재하지 않는 메세지"),
    INVALID_MESSAGE(400,"INVALID_MESSAGE","잘못된 메세지 형식"),
    MESSAGE_AUTHOR_ONLY(403, "MESSAGE_AUTHOR_ONLY","메시지 작성자와 일치하지 않는 사용자"),
    //BINARY_CONTENT
    BINARY_CONTENT_NOT_FOUND(404,"BINARY_CONTENT_NOT_FOUND","존재하지 않는 바이너리 컨텐츠")


    ;

    private final int status;
    private final String code;
    private final String message;

    ExceptionCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
