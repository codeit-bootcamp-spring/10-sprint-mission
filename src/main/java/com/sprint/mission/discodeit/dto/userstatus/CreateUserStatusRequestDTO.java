package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record CreateUserStatusRequestDTO(
        UUID userId,
        Instant lastLoginAt
) { }