package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

// 클라이언트에게 반환할 채널 정보
public record ChannelResponse (
        UUID id,
        String name,
        String description,
        ChannelType type,
        boolean isPublic,
        Instant createdAt,
        Instant updatedAt
) {
}
