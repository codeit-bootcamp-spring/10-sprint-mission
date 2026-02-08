package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID channelId,
        UUID ownerId,
        String channelName,
        String channelDescription
) {
}
