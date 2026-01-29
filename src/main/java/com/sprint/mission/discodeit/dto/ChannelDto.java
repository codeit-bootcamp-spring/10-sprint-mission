package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    public record CreatePublicRequest(
            @NotBlank
            String name,
            String description

    ) {}
    public record CreatePrivateRequest(
            List<UUID> memberIds

    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description,
            List<UUID> memberIds,
            Instant lastMessageAt
    ) {}

    public record UpdatePublicRequest(
            @NotBlank
            String newName,
            String newDescription

    ){}
}
