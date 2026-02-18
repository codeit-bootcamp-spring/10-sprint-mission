package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;

public record DeleteMessageResponseDTO(
        Instant timestamp,
        int status,
        String message
) { }
