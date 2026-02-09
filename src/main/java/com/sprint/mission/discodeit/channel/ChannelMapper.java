package com.sprint.mission.discodeit.channel;

import com.sprint.mission.discodeit.channel.dto.ChannelInfo;
import com.sprint.mission.discodeit.channel.dto.PrivateChannelCreateInfo;
import com.sprint.mission.discodeit.channel.dto.PrivateChannelInfo;
import com.sprint.mission.discodeit.channel.dto.PublicChannelInfo;
import com.sprint.mission.discodeit.common.ChannelType;

import java.time.Instant;

public final class ChannelMapper {

    private ChannelMapper() {}

    public static ChannelInfo toChannelInfo(Channel channel, Instant lastMessageTime) {
        if(channel.getChannelType() == ChannelType.PRIVATE) {
            return new ChannelInfo(channel.getId(),
                    null,
                    channel.getChannelType(),
                    null,
                    lastMessageTime,
                    channel.getUserIds());
        } else {
            return new ChannelInfo(
                    channel.getId(),
                    channel.getChannelName(),
                    channel.getChannelType(),
                    channel.getDescription(),
                    null,
                    channel.getUserIds()
            );
        }
    }

    public static PublicChannelInfo toPublicChannelInfo(Channel channel) {
        return new PublicChannelInfo(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelType(),
                channel.getDescription()
        );
    }

    public static PrivateChannelInfo toPrivateChannelInfo(Channel channel) {
        return new PrivateChannelInfo(
                channel.getId(),
                channel.getChannelType()
        );
    }

    public static PrivateChannelCreateInfo toPrivateChannelCreateInfo(Channel channel) {
        return new PrivateChannelCreateInfo(channel.getUserIds());
    }

    public static Channel toChannel(ChannelInfo channelInfo) {
        return new Channel(
                channelInfo.channelName(),
                channelInfo.channelType(),
                channelInfo.description()
        );
    }

    public static Channel toChannel(PublicChannelInfo channelInfo) {
        return new Channel(
                channelInfo.channelName(),
                ChannelType.PUBLIC,
                channelInfo.description()
        );
    }

    public static Channel toChannel(PrivateChannelCreateInfo channelInfo) {
        return new Channel(
                null,
                ChannelType.PRIVATE,
                null
        );
    }
}
