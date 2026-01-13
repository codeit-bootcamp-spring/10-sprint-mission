package com.sprint.mission.discodeit.validation;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ValidationMethods {
    // User ID null 검증
    public static void validateUserId(UUID userId) {
        Objects.requireNonNull(userId, "User ID가 null입니다.");
    }

    // Channel ID null 검증
    public static void validateChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "Channel ID가 null입니다.");
    }

    // Message ID null 검증
    public static void validateMessageId(UUID messageId) {
        Objects.requireNonNull(messageId, "Message ID가 null입니다.");
    }

    // String null or blank 검증
    // User: email이나 password 등
    // Channel: channelName 등
    public static void validateString(String str, String fieldName) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(fieldName + "이(가) 입력되지 않았습니다.");
        }
    }

    // 이메일 중복 확인
    public static void duplicateEmail(Map<UUID, User> data, String email) {
        data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .ifPresent(user -> {
                    throw new IllegalStateException("동일한 이메일이 존재합니다");
                });
    }
}
