package com.sprint.mission.discodeit.validation;

import java.util.Objects;
import java.util.UUID;

public class ValidationMethods {
    // 통합 ID `null` 검증
    public static void validateId(UUID id) {
        Objects.requireNonNull(id, "ID가 null입니다.");
    }

    // String `null` or `blank` 검증
    // User: email이나 password, partialName 등
    // Channel: channelName, partialChannelName 등
    // Message: content 등
    public static void validateNullBlankString(String str, String fieldName) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(fieldName + "이(가) 입력되지 않았습니다.");
        }
    }
}
