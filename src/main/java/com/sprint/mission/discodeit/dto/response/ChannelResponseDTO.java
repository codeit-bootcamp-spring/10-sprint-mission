package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelResponseDTO (
    UUID id,
    UUID userId,
    String channelName,
    List<UUID> members,
    ChannelType channelType,
    String description,
    Instant createdAt,
    Instant updatedAt,
    Instant lastMessageAt
) {

}
