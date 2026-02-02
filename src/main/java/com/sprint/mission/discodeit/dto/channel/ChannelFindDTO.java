package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindDTO(
        Channel channel,
        Instant lastMessageTime,
        List<UUID> userList
) {
}
