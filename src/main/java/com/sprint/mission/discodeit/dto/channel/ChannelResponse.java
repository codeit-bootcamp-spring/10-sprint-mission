package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String type,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantUserIds

) {}
