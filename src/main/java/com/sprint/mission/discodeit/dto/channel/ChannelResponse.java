package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ChannelVisibility;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 클라이언트에게 반환할 채널 정보
public record ChannelResponse (
        UUID id,
        String name,
        String description,
        ChannelType type,
        ChannelVisibility visibility,
        Instant lastMessageAt, // 가장 최근 메시지의 시간 정보
        List<UUID> memberIds,
        Instant createdAt,
        Instant updatedAt
) {
}
