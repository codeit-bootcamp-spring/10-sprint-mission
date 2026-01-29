package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record ChannelResponse (
        UUID id,
        String name,
        String description,
        ChannelType type,
        boolean isPublic,
        Instant createdAt,
        Instant updatedAt
) {
}
