package com.sprint.mission.discodeit.util;

public class Validator {
    private Validator() {}

    // null 검증
    public static <T> void validateNotNull(T value, String errMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errMessage);
        }
    }

    // 빈 문자열 검증
    public static void validateNotBlank(String value, String errMessage) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(errMessage);
        }
    }
}
