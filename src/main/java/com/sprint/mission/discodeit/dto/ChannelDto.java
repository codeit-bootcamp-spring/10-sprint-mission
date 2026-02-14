package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    public record CreatePublicRequest(
            String name,
            String description
    ) {}

    public record CreatePrivateRequest(
            List<UUID> memberIds
    ) {}

    public record UpdateRequest(
            UUID id,
            String name,
            String description
    ) {}

    public record Response(
            UUID id,
            String name,
            String description,
            ChannelType type,
            Instant lastMessageAt,
            List<UUID> memberIds
    ) {}
}
