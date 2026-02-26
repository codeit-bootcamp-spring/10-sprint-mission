package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    ChannelType type,
    String name,
    String description,
    List<UUID> participantIds, // private 채널인 경우
    Instant lastMessageAt
) {

}

