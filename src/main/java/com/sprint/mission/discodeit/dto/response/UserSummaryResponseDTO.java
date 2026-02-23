package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserSummaryResponseDTO(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId
) {}
