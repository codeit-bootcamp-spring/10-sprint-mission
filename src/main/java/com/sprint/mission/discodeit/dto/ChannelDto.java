package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
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
            String name,
            String description,
            List<UUID> participantIds,
            Instant lastReadAt
    ) {
        public static Response of(Channel channel, List<UUID> allUserIds, Instant lastReadAt) {
            return new Response(
                    channel.getId(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    allUserIds,
                    lastReadAt
            );
        }
    }

    public record Update(
            String name,
            String description
    ) {}
}
