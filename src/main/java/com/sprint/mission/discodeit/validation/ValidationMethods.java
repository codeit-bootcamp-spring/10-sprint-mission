package com.sprint.mission.discodeit.validation;

import java.util.Objects;
import java.util.UUID;

public class ValidationMethods {
    // 통합 ID `null` 검증
    public static void validateId(UUID id) {
        Objects.requireNonNull(id, "ID가 null입니다.");
    }
}
