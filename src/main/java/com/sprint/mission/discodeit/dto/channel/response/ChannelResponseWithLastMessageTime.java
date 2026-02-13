package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseWithLastMessageTime(
        UUID id,
        UUID ownerId,
        ChannelType channelType,
        String channelName,
        String channelDescription,
        List<UUID> channelMembersIds,
        Instant lastMessageTime
) {

}
