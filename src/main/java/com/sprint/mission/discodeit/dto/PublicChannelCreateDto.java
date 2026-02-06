package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.IsPrivate;

import java.util.UUID;

public record PublicChannelCreateDto(
        String name,
        UUID ownerId,
        String description
) {
}
