package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.IsPrivate;

import java.time.Instant;
import java.util.UUID;

public record PublicChannelnfoDto(
        String name,
        IsPrivate isPrivate,
        UUID ownerId,
        Instant lastMessageTime
) {
}
