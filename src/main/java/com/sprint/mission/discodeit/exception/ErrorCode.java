package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "Internal server error"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "Entity not found"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "U002", "Duplicate user info (email or username)"),
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "User status not found"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CH001", "Channel not found"),
    DUPLICATE_CHANNEL_NAME(HttpStatus.BAD_REQUEST, "CH002", "Channel name already exists"),
    PRIVATE_CHANNEL_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "CH003", "Private channel cannot be updated"),
    CHANNEL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CH004", "Channel access denied"),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "Message not found"),
    MESSAGE_AUTHOR_NOT_IN_CHANNEL(HttpStatus.BAD_REQUEST, "M002", "Author is not in the channel"),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "Binary content not found"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "B002", "Invalid file format");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
