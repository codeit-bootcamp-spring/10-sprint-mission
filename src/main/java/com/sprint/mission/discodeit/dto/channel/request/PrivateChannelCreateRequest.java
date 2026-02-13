package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID ownerId,
        List<UUID> participantIds
) {
}
