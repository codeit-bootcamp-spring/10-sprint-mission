package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ChannelDto {
    private ChannelDto() {}

    public record createPrivateRequest(ChannelType channelType, String title, String description) {}
    public record createPublicRequest(ChannelType channelType, String title, String description) {}
    public record updatePublicRequest(String title, String description) {}
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           ChannelType channelType, String title, String description,
                           Instant lastMessageAt, List<UUID> participantIds) {}
}
