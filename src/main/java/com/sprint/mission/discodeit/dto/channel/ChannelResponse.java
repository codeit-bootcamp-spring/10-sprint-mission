package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        UUID ownerId,
        ChannelType channelType,
        String channelName,
        String channelDescription,
        List<UUID> channelMembersIds,
        Instant lastMessageTime
) {

}
