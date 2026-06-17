package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/*
    ErrorCode
    ---------
    프로젝트 내 커스텀 예외의 HTTP 상태값 및 에러 메시지 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "Missing request parameter"),
    METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "Method argument type mismatch"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    DUPLICATE_VALUE_NOT_UPDATE(HttpStatus.BAD_REQUEST, "The update value is the same as the current value"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have the required permissions to perform this action"),

    // Auth
    JWT_TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Invalid or Expired refresh token"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id not found"),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "User with username already exists"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "User with email already exists"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel with id not found"),
    ACCESS_DENIED_PRIVATE_CHANNEL(HttpStatus.FORBIDDEN, "Access denied for private channel members"),
    PRIVATE_CHANNEL_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "Private channel cannot be updated"),
    CHANNEL_PARTICIPANT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User is already a participant of this channel"),
    CHANNEL_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "User is not a participant of this channel"),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message with id not found"),

    // ReadStatus
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ReadStatus with id not found"),
    DUPLICATE_READ_STATUS(HttpStatus.BAD_REQUEST, "ReadStatus with userId and channelId already exists"),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Binary content with id not found"),
    BINARY_CONTENT_FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while processing file"),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification with id not found"),
    ACCESS_DENIED_NOTIFICATION(HttpStatus.FORBIDDEN, "Access denied for notification");

    private final HttpStatus httpStatus;
    private final String message;
}
