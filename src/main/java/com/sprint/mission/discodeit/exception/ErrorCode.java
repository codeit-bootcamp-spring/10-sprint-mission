package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // User
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    DUPLICATE_USER(400, "중복된 user입니다"),
    DUPLICATE_USERNAME(400, "중복된 username입니다"),
    DUPLICATE_EMAIL(400, "중복된 email입니다"),

    // UserStatus
    USERSTATUS_NOT_FOUND(404, "userStatus를 찾을 수 없습니다"),
    USERSTATUS_ALREADY_EXISTS(400, "이미 존재하는 userStatus입니다"),

    // Channel
    CHANNEL_NOT_FOUND(404, "채널을 찾을 수 없습니다"),
    USER_ALREADY_IN_CHANNEL(409, "이미 참가한 참가자입니다"),
    USER_NOT_IN_CHANNEL(404, "채널에 참가하지 않은 유저입니다"),
    DUPLICATE_TITLE(409, "중복된 채널명 입니다"),
    PRIVATE_CHANNEL_NOT_EDITABLE(400, "Private 채널은 수정할 수 없습니다"),

    // BinaryContent
    BINARYCONTENT_NOT_FOUND(404, "존재하지 않는 binaryContent입니다"),

    // Message
    MESSAGE_WRITE_FORBIDDEN(403, "Message를 생성할 권한이 없습니다"),
    MESSAGE_NOT_FOUND(404, "존재하지 않는 메시지입니다"),

    // ReadStatus
    READSTATUS_ALREADY_EXISTS(400, "이미 존재하는 readStatus입니다"),
    READSTATUS_NOT_FOUND(404, "존재하지 않는 readStatus입니다");


    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return this.name();
    }
}
