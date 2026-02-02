package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    public record CreateRequest(
            UUID userID,
            Instant lastOnlineAt
    ) {}

    public record UpdateRequest(
            UUID id,
            Instant lastOnlineAt
    ) {}

    public record Response(
            UUID id,
            UUID userId,
            Instant lastOnlineAt,
            boolean isOnline
    ) {}
}
