package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelInfo;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateInfo;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelInfo;
import com.sprint.mission.discodeit.dto.channel.PublicChannelInfo;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

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
}
