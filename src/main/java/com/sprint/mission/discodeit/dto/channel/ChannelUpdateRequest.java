package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID ownerId,
        UUID channelId,
        ChannelType channelType,
        String channelName,
        String channelDescription
) {
}
