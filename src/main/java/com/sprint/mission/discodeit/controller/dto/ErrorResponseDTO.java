package com.sprint.mission.discodeit.controller.dto;

import java.time.Instant;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) { }
