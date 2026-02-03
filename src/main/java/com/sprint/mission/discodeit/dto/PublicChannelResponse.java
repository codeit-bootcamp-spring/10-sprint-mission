package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record PublicChannelResponse(
        UUID id,
        String name,
        String description,
        ChannelType channelType,
        Instant lastMessageAt
) implements ChannelResponse {
    public static PublicChannelResponse of(
            Channel channel,
            Instant lastMessageAt
    ) {
        return new PublicChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getChannelType(),
                lastMessageAt
        );
    }
}
