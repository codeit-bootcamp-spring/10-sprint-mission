package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelInfo(
        UUID channelId,
        String channelName,
        ChannelType channelType,
        String description,
        Instant lastMessageTime,
        List<UUID> userIds
) {}
