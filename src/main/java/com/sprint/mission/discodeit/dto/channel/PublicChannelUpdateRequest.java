package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record PublicChannelUpdateRequest(
        UUID id,
        String newName,
        String newDescription
) {
}
