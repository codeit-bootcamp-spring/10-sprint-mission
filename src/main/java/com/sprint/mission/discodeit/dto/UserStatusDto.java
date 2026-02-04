package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {
    public record CreateRequest(
            UUID userId
    ){}

    public record Response(
            UUID id,
            UUID userId,
            Instant updatedAt,
            boolean isOnline
    ){}

    public record UpdateRequest(
            UUID id,
            UUID userId
    ){}
}
