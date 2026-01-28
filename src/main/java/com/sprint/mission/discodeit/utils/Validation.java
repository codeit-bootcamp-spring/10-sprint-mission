package com.sprint.mission.discodeit.utils;

import java.util.Collection;
import java.util.function.Predicate;

public class Validation {
    // 인스턴스화 방지 (new ValidationUtils() 금지)
    private Validation(){};

    public static void notBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
    }

    public static <T> void noDuplicate(Collection<T> collection, Predicate<T> condition, String message) {
        if (collection.stream().anyMatch(condition)) {
            throw new IllegalStateException(message);
        }
    }
}
