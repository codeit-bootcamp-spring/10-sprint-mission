package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record UpdateChannelRequestDTO(
        UUID channelId,
        String newChannelName,
        String newChannelDescription,
        ChannelType channelType
) { }
