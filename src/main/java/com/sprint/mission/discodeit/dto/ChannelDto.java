package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    // 요청 DTO
    public record ChannelRequest(
            String name,
            String description
    ) {}

    public interface ChannelResponse {
    }

    public record ChannelResponsePublic(
            UUID channelId,
            String name,
            String description,
            Channel.channelType type,
            Instant lastMessageAt
    ) implements ChannelResponse {
        public static ChannelResponsePublic from(Channel channel) {
            return new ChannelResponsePublic(
                    channel.getId(),
                    channel.getName(),
                    channel.getDescription(),
                    channel.getType(),
                    channel.getLastMessageAt()
            );
        }
    }

    public record ChannelResponsePrivate(
            UUID channelId,
            Instant lastMessageAt,
            List<UUID> userIds,
            Channel.channelType type,
            String serverId
    ) implements ChannelResponse {
        public static ChannelResponsePrivate from(Channel channel) {
            return new ChannelResponsePrivate(
                    channel.getId(),
                    channel.getLastMessageAt(),
                    channel.getUserIds(),
                    channel.getType(),
                    channel.getPrivateServerId()
            );
        }
    }
}

