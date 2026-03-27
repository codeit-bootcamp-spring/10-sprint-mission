package com.sprint.mission.discodeit.controller.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType
) { }
