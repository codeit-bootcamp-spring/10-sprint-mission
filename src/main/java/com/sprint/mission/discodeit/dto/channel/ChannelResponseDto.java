package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(UUID id,
                                 ChannelType type,
                                 String channelName,
                                 String channelDescription,
                                 Instant lastMessageAt,
                                 List<UUID> joinedUserIds) {
}
