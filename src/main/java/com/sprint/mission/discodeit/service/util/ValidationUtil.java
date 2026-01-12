package com.sprint.mission.discodeit.service.util;

public class ValidationUtil {
    private ValidationUtil() {}

    // 유효성 검사 (수정)
    public static void validateField(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
