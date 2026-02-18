package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;

public record DeleteUserResponseDTO(
        Instant timestamp,
        int status,
        String message
) { }
