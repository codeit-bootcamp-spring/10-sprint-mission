package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PrivateChannelCreateRequestDTO (
    @NotNull
    UUID userId,

    List<UUID> memberIds
) {

}
