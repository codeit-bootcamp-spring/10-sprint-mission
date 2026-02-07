package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDTO(
        UUID userStatusId,
        UUID userId,
        Instant lastAccessTime
) {}
