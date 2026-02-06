package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelCreatePrivateRequest(
        ChannelType type,
        List<UUID> userIds
) {
}
