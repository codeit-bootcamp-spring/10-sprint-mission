package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse (
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt, // 가장 최근 메시지 시간
        List<UUID> participantIds // PRIVATE 채널인 경우 참여자 id 목록
) {}
