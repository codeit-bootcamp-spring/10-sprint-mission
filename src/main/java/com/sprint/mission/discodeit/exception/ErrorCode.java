package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "로그인에 실패하였습니다."),
    INVALID_LOGIN_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_002", "알 수 없는 오류가 발생했습니다"),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_004", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "해당 유저를 찾을 수 없습니다"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "USER_002", "동일한 이메일 또는 사용자명이 이미 존재합니다"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CHANNEL_001", "해당 채널을 찾을 수 없습니다."),
    INVALID_PRIVATE_CHANNEL_CREATED(HttpStatus.BAD_REQUEST, "CHANNEL_002", "비공개 채널의 참여자 수는 2명 이상이어야 합니다"),
    INVALID_PRIVATE_CHANNEL_MODIFIED(HttpStatus.BAD_REQUEST, "CHANNEL_003", "비공개 채널은 수정할 수 없습니다"),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "해당 메시지를 찾을 수 없습니다"),
    MESSAGE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MESSAGE_002", "메시지를 전송할 수 없습니다"),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BINARY_CONTENT_001", "해당 파일을 찾을 수 없습니다"),

    // ReadStatus
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "READ_STATUS_001", "해당 ReadStatus를 찾을 수 없습니다"),
    READ_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "READ_STATUS_002", "이미 해당 유저와 채널 간 ReadStatus가 존재합니다"),

    // UserStatus
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_STATUS_001", "해당 UserStatus를 찾을 수 없습니다"),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_STATUS_002", "이미 해당 유저는 UserStatus를 가지고 있습니다"),

    // File
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_001", "파일 처리 중 오류가 발생했습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_002", "잘못된 파일 타입입니다"),

    // S3 Upload
    S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_001", "S3 파일 업로드 중 오류가 발생했습니다"),
    S3_UPLOAD_BUCKET_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "S3_002", "S3 업로드 대상 버킷이 존재하지 않습니다"),
    S3_UPLOAD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S3_003", "S3 버킷에 대한 쓰기 권한이 없습니다"),

    // S3 Download
    S3_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_004", "S3 파일 다운로드 중 오류가 발생했습니다"),
    S3_DOWNLOAD_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3_005", "S3 버킷에 해당 파일이 존재하지 않습니다"),
    S3_DOWNLOAD_BUCKET_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "S3_006", "S3 다운로드 대상 버킷이 존재하지 않습니다"),
    S3_DOWNLOAD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S3_007", "S3 버킷에 대한 읽기 권한이 없습니다"),
    S3_DOWNLOAD_PRESIGN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_008", "S3 파일 다운로드 URL 생성에 실패했습니다"),

    // ETC
    DATABASE_CONFLICT(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "DB에 데이터 저장 중 오류가 발생했습니다"),
    DATA_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_002", "데이터 무결성 오류가 발생했습니다");

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;

    public int getStatusValue() {
        return this.status.value();
    }
}
