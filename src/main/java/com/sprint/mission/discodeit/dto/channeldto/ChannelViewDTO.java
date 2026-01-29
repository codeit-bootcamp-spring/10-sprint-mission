package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelViewDTO(
        Instant latestMessageCreatedAt,
        Instant latestMessageUpdatedAt,
        List<UUID> userID
) {
}
