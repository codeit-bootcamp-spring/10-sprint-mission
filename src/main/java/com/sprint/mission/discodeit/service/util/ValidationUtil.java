package com.sprint.mission.discodeit.service.util;

public class ValidationUtil {
    // 유효성 검사 (공백)
    public void validateString(String value, String errorMessage) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // 유효성 검사 (중복)
    public void validateDuplicateValue(String current, String update, String errorMessage) {
        if (current.equals(update)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}