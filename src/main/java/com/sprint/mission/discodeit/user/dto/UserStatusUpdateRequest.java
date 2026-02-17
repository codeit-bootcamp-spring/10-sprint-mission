package com.sprint.mission.discodeit.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        Instant lastConnectedAt) {
}
