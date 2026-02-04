package com.sprint.mission.discodeit.dto.channel;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID channelId,
        String channelName,
        String description,
        boolean isPrivate,
        Instant lastMessageTime,
        List<UUID> participantIds
) {}
