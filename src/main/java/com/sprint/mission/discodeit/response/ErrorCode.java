package com.sprint.mission.discodeit.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "Invalid request"),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found"),
    USER_STATUS_NOT_FOUND("USER_STATUS_NOT_FOUND", HttpStatus.NOT_FOUND, "User status not found"),
    CHANNEL_NOT_FOUND("CHANNEL_NOT_FOUND", HttpStatus.NOT_FOUND, "Channel not found"),
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", HttpStatus.NOT_FOUND, "Message not found"),
    BINARY_CONTENT_NOT_FOUND("BINARY_CONTENT_NOT_FOUND", HttpStatus.NOT_FOUND, "Binary content not found"),
    IMAGE_BINARY_CONVERSION_FAILED("IMAGE_BINARY_CONVERSION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "Image binary conversion failed"),
    READ_STATUS_NOT_FOUND("READ_STATUS_NOT_FOUND", HttpStatus.NOT_FOUND, "Read status not found"),

    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT, "Username already exists"),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT, "Email already exists"),
    USER_STATUS_ALREADY_EXISTS("USER_STATUS_ALREADY_EXISTS", HttpStatus.CONFLICT, "User status already exists"),
    READ_STATUS_ALREADY_EXISTS("READ_STATUS_ALREADY_EXISTS", HttpStatus.CONFLICT, "Read status already exists"),

    INVALID_PASSWORD("INVALID_PASSWORD", HttpStatus.UNAUTHORIZED, "Invalid password"),
    CHANNEL_UPDATE_FORBIDDEN("CHANNEL_UPDATE_FORBIDDEN", HttpStatus.FORBIDDEN, "Channel update not allowed"),
    MESSAGE_SENDER_MISMATCH("MESSAGE_SENDER_MISMATCH", HttpStatus.FORBIDDEN, "Not message sender");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;
}
