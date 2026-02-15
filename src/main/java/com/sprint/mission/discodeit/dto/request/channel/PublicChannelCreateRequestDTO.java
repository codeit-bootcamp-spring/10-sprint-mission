package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PublicChannelCreateRequestDTO (
    @NotNull
    UUID userId,

    @NotNull
    String channelName,

    @NotBlank
    String description
) {

}
