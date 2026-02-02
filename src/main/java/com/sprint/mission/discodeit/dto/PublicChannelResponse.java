package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;

public record PublicChannelResponse(
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
                channel.getName(),
                channel.getDescription(),
                channel.getChannelType(),
                lastMessageAt
        );
    }
}
