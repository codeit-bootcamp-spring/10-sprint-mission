package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDTO(
        UUID userId,
        UUID userStatusId,
        UserStatusType statusType,
        Instant lastLoginAt
) { }
