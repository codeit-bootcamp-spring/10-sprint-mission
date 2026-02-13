package com.sprint.mission.discodeit.userstatus.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusInfo(
        UUID statusId,
        UUID userId,
        Instant lastOnlineAt,
        boolean isOnline
)
{}
