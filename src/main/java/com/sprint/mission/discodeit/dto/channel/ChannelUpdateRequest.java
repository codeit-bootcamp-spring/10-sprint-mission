package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

// 채널 정보 수정 시 필요한 데이터
public record ChannelUpdateRequest (
        UUID id,
        String name,
        String description,
        ChannelType type,
        boolean isPublic
) {
}
