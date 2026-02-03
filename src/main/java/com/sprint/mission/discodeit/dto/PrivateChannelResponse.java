package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelResponse(
        UUID id,
        Set<UUID> memberIds,
        ChannelType channelType,
        Instant lastMessageAt
) implements ChannelResponse {
    public static PrivateChannelResponse of(
            Channel channel,
            Instant lastMessageAt
    ) {
        return new PrivateChannelResponse(
                channel.getId(),
                channel.getMemberIds(),
                channel.getChannelType(),
                lastMessageAt
        );
    }
}
