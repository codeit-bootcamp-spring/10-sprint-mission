package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.IsPrivate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelInfoDto(
        UUID id,
        String name,
        IsPrivate isPrivate,
        String description,
        UUID ownerId,
        Instant lastMessageAt,
        List<UUID> users
) {
}
