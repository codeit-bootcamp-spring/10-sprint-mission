package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record PublicChannelInfo(
        UUID channelId,
        String channelName,
        ChannelType channelType,
        String description
) {}
