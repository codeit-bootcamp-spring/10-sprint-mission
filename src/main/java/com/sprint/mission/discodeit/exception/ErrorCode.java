package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PRIVATE_CHANNEL_UPDATE(400, "PRIVATE_CHANNEL_UPDATE", "개인 채널은 수정할 수 없습니다."),
    FILE_UPLOAD_FAIL(400, "FILE_UPLOAD_FAIL", "파일 업로드가 실패했습니다."),
    FILE_SAVE_FAIL(400, "FILE_SAVE_FAIL", "파일 저장에 실패했습니다."),
    FILE_READ_FAIL(400, "FILE_READ_FAIL", "파일 읽기에 실패했습니다."),
    STORAGE_INIT_FAIL(400, "STORAGE_INIT_FAIL", "저장소 초기화가 실패했습니다."),
    WRONG_PASSWORD( 401,"WRONG_PASSWORD","잘못된 비밀번호입니다."),
    USER_NOT_FOUND(404,"USER_NOT_FOUND","유저를 찾을 수 없습니다."),
    USERSTATUS_NOT_FOUND( 404,"USERSTATUS_NOT_FOUND","해당 유저의 상태를 찾을 수 없습니다."),
    BINARYCONTENT_NOT_FOUND(404,"BINARYCONTENT_NOT_FOUND","파일 컨텐츠를 찾을 수 없습니다"),
    CHANNEL_NOT_FOUND(404,"CHANNEL_NOT_FOUND","채널을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(404,"MESSAGE_NOT_FOUND","메시지를 찾을 수 없습니다."),
    READSTATUS_NOT_FOUND( 404,"READSTATUS_NOT_FOUND","읽음 상태를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409,"DUPLICATE_EMAIL","중복된 이메일입니다."),
    USERSTATUS_EXIST(409,"USERSTATUS_EXIST","해당 유저의 상태가 이미 있습니다"),
    READSTATUS_EXIST( 409,"READSTATUS_EXIST","이미 존재하는 읽음 상태가 있습니다.");

    private final int status;
    private final String code;
    private final String message;
}
