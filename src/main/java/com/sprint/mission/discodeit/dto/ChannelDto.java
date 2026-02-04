package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {
    public record CreateRequest(
            ChannelType type,
            String name,
            String description
    ) {}


    @Builder
    public record Response(
            UUID id,
            ChannelType type,
            String name,
            String description,
            Instant lastMessageAt,
            List<UUID> memberIdList

    ) {}

    public record UpdateRequest (
            UUID channelId,
            String newName,
            String newDescription
    ) {}
}
