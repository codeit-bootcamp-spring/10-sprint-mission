package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        UUID id,
        ChannelType channelType,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant lastMessageAt,
        List<UUID> memberIds//public이면 모든 사용자의 아이디
) {
}
