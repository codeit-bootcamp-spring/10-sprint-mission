package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID channelId,
        UUID ownerId,
        ChannelType channelType,
        String channelName,
        String channelDescription,
        List<UUID> channelMembersIds
) {
}
