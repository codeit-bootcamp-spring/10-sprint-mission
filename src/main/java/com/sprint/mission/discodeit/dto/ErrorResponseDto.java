package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path
) {
}
