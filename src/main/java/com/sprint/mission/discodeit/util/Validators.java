package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestCreateDto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Validators {

    public static void requireNotBlank(String value, String fieldName) {
        requireNonNull(value, fieldName + "은 null이 될 수 없습니다.");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "에 공백을 입력할 수 없습니다.");
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        Objects.requireNonNull(value, fieldName + "은 null이 될 수 없습니다.");
    }

    public static void validationMessage(String content) {
        requireNotBlank(content, "content");
    }

    public static void validateCreateMessageRequest(MessageRequestCreateDto request) {
        requireNonNull(request, "request");
        requireNonNull(request.channelId(), "channelId");
        requireNonNull(request.authorId(), "authorId");
    }

    public static void validateCreateReadStatusRequest(ReadStatusRequestCreateDto request) {
        requireNonNull(request, "request");
        requireNonNull(request.channelId(), "channelId");
        requireNonNull(request.userId(), "userId");
    }

    public static void validationUser(String userName, String userEmail, String userPassword) {
        requireNotBlank(userName, "userName");
        requireNotBlank(userEmail, "userEmail");
        requireNotBlank(userPassword, "userPassword");
    }

    public static void validateCreatePublicChannel(String name, String description) {
        requireNotBlank(name, "channelName");
        requireNotBlank(description, "channelDescription");
    }

    public static void validateCreatePrivateChannel(List<UUID> joinedUserIds) {
        if (joinedUserIds == null || joinedUserIds.isEmpty()) {
            throw new IllegalArgumentException("joinedUserIds는 필수입니다.");
        }
    }


}
