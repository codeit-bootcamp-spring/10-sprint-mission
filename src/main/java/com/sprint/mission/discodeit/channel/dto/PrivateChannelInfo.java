package com.sprint.mission.discodeit.channel.dto;

import com.sprint.mission.discodeit.common.ChannelType;

import java.util.UUID;

public record PrivateChannelInfo(
        UUID channelId,
        ChannelType channelType
) {}
