package com.sprint.mission.discodeit.dto.channel.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindResponse(
    UUID channelId,
    String name,
    String descriptions,
    Instant lastMessageTime,
    List<UUID> userIds
) {}
