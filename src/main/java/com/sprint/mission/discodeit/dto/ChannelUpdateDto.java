package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.IsPrivate;

import java.util.UUID;

public record ChannelUpdateDto(
        String name,
        IsPrivate isPrivate,
        UUID ownerId,
        String description
) {
}
