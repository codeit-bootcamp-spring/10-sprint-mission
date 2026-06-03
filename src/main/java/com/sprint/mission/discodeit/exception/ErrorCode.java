package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT("입력값이 유효하지 않습니다."),
    NO_CHANGE_VALUE("변경사항이 없습니다."),

    // User
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL("이미 존재하는 이메일입니다."),
    DUPLICATED_USERNAME("이미 존재하는 사용자 이름입니다."),
    LOGIN_FAILED("아이디 또는 비밀번호가 잘못되었습니다."),

    PROFILE_UPLOAD_FAILED("프로필 이미지 업로드에 실패했습니다."),
    PROFILE_NOT_FOUND("프로필 이미지를 찾을 수 없습니다."),
    PROFILE_READ_FAILED("프로필 이미지 처리 중 오류가 발생했습니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND("바이너리 컨텐츠를 찾을 수 없습니다."),
    BINARY_CONTENT_SAVE_FAILED("바이너리 컨텐츠 저장에 실패했습니다."),
    BINARY_CONTENT_READ_FAILED("바이너리 컨텐츠 조회에 실패했습니다."),
    BINARY_CONTENT_STORAGE_INIT_FAILED("디렉터리 초기화에 실패했습니다."),
    PRESIGNED_URL_CREATE_FAILED("Presigned URL 생성에 실패했습니다."),
    AWS_SERVER_CONNECT_FAILED("AWS에 연결 실패했습니다."),

    // Channel
    PRIVATE_CHANNEL_PARTICIPANT_REQUIRED("비공개 채널에는 참가자가 있어야 합니다."),
    PRIVATE_CHANNEL_CANNOT_BE_UPDATED("비공개 채널은 수정할 수 없습니다."),
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),

    // Message
    ATTACHMENTS_UPLOAD_FAILED("첨부파일 업로드에 실패했습니다."),
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),

    // ReadStatus
    DUPLICATED_READ_STATUS("이미 존재하는 마지막 메시지 읽음 상태 정보가 있습니다."),
    READ_STATUS_NOT_FOUND("마지막 메시지 읽음 상태 정보를 찾을 수 없습니다."),

    // Security - Token
    INVALID_REFRESH_TOKEN("유효하지 않은 Refresh Token입니다."),

    // Server
    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."); // 커스텀 예외로 처리하지 못하는 예외 코드

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
