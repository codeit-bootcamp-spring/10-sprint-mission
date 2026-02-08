package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelOwnerChangeRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID currentUserId,
        @NotNull(message = "ID가 null입니다.")
        UUID newOwnerId
) {
}
