package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        Instant lastMessageTime,
        String name,
        String description,
        ChannelType type,
        List<UUID> joinedUser //private 일때만 사용
) {
}
