package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth
    LOGIN_FAIL(400, "A400", "로그인에 실패했습니다."),

    // User
    USER_NOT_FOUND(404, "U100", "유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(400, "U200", "이미 존재하는 유저입니다."),
    USERNAME_ALREADY_EXISTS(409, "U201", "이미 사용중인 username입니다."),
    EMAIL_ALREADY_EXISTS(409, "U202", "이미 사용중인 email입니다."),
    USERNAME_UNCHANGED(400, "U301", "현재 사용중인 username과 동일합니다."),
    EMAIL_UNCHANGED(400, "U302", "현재 사용중인 email과 동일합니다."),

    // Channel
    CHANNEL_NOT_FOUND(404, "C100", "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE(400, "C200", "비공개 채널은 수정할 수 없습니다."),
    CHANNEL_NAME_ALREADY_EXISTS(409, "C301", "이미 사용중인 channelName입니다."),
    CHANNEL_DESCRIPTION_UNCHANGED(400, "C401", "현재 사용중인 description과 동일합니다."),
    CHANNEL_NAME_IS_BLANK(400, "C501", "채널 이름은 공백일 수 없습니다."),
    CHANNEL_DESCRIPTION_IS_BLANK(400, "C502", "채널 설명은 공백일 수 없습니다."),

    // Message
    MESSAGE_NOT_FOUND(404, "M100", "메시지를 찾을 수 없습니다."),
    MESSAGE_CONTENT_IS_BLANK(400, "M200", "메시지의 content는 비어있을 수 없습니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(404, "B100", "파일을 찾을 수 없습니다."),
    BINARY_CONTENT_DATA_IS_NULL(400, "B201", "data는 null/empty값일 수 없습니다."),
    BINARY_CONTENT_CONTENT_TYPE_IS_NULL(400, "B202", "contentType은 null/empty값일 수 없습니다."),
    BINARY_CONTENT_FILENAME_IS_NULL(400, "B203", "filename은 null/empty값일 수 없습니다."),
    BINARY_CONTENT_CAN_NOT_READ(500, "B300", "파일을 읽을 수 없습니다."),
    BINARY_CONTENT_CAN_NOT_SAVE(500, "B401", "파일을 저장할 수 없습니다."),
    BINARY_CONTENT_FAIL_TO_SAVE_STORAGE(500, "B402", "파일을 storage에 저장하기에 실패했습니다."),

    // ReadStatus
    READ_STATUS_NOT_FOUND(404, "RS100", "ReadStatus를 찾을 수 없습니다."),
    READ_STATUS_ALREADY_EXISTS(400, "RS200", "이미 존재하는 ReadStatus입니다."),
    LAST_READ_AT_IS_NULL(400, "RS300", "lastReadAt은 null값일 수 없습니다."),

    // UserStatus
    USER_STATUS_NOT_FOUND(404, "US100", "UserStatus를 찾을 수 없습니다."),

    // Invalid input
    ID_CAN_NOT_BE_NULL(400, "I100", "id값은 null일 수 없습니다."),
    DTO_CAN_NOT_BE_NULL(400, "I101", "dto는 null일 수 없습니다."),
    ;

    private final int status;
    private final String errorType;
    private final String message;
}
