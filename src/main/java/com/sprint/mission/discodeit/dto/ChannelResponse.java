package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;

public sealed interface ChannelResponse
        permits PublicChannelResponse, PrivateChannelResponse {

    static ChannelResponse of(Channel channel, Instant lastMessageAt) {
        if (channel.isPrivate()) {
            return PrivateChannelResponse.of(channel, lastMessageAt);
        }

        return PublicChannelResponse.of(channel, lastMessageAt);
    }
}
