package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

// 채널 생성 시 필요한 데이터
public record ChannelCreateRequest(
        String name,
        String description,
        ChannelType type,
        boolean isPublic
) {
}
