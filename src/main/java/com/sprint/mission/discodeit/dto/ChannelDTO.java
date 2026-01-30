package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDTO {
    public record CreatePublic(
            String name,
            String description
    ) {}

    public record CreatePrivate(
            List<UUID> userIds
    ) {}

    public record Response(
            UUID id,
            ChannelType type,
            String channelName,
            String description,
            List<UUID> userIds,
            Instant lastReadAt
    ) {
        public static Response of(Channel channel, List<UUID> allUserIds, Instant lastReadAt) {
            return new Response(
                    channel.getId(),
                    channel.getType(),
                    channel.getChannelName(),
                    channel.getDescription(),
                    allUserIds,
                    lastReadAt
            );
        }
    }

    public record Update(
            UUID id,
            String name,
            String description
    ) {}
}
