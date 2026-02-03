package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public sealed interface ChannelResponse
        permits PublicChannelResponse, PrivateChannelResponse {
    UUID id();
    ChannelType channelType();
    Instant lastMessageAt();

    static ChannelResponse of(Channel channel, Instant lastMessageAt) {
        if (channel.isPrivate()) {
            return PrivateChannelResponse.of(channel, lastMessageAt);
        }

        return PublicChannelResponse.of(channel, lastMessageAt);
    }
}
