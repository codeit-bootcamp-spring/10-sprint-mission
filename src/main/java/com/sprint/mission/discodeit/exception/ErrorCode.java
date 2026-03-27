package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
    USER_NOT_FOUND("사용자 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("PRIVATE 채널은 수정할 수 없습니다."),
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다."),
    DUPLICATE_USERNAME("이미 존재하는 이름입니다."),
    MESSAGE_NOT_FOUND("메세지를 찾을 수 없습니다."),
    BINARYCONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    USERSTATUS_NOT_FOUND("사용자상태를 찾을 수 없습니다."),
    PASSWORD_WRONG("패스워드가 일치하지 않습니다."),
    READSTATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    BINARYCONTENT_ALREADY_EXISTS("파일이 이미 존재합니다."),
    FILE_STORAGE_ERROR("파일 저장 중 오류가 발생했습니다.");


    private final String message;

    ErrorCode(String message){
        this.message = message;

    }

    public String getMessage() {
        return message;
    }
}
