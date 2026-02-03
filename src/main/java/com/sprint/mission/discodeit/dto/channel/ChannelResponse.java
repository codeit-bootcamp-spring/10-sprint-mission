package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant latestMessageTime,
        List<UUID> userIds
) {
    public static ChannelResponse from(Channel channel, Instant latestMessageTime, List<UUID> userIds) {
        List<UUID> safeUserIds = (userIds != null) ? List.copyOf(userIds) : List.of();

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageTime,
                safeUserIds
        );
    }
}
