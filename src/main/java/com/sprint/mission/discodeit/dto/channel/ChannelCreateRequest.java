package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelCreateRequest(
        String name,
        String description,
        ChannelType type,
        boolean isPublic
) {
}
