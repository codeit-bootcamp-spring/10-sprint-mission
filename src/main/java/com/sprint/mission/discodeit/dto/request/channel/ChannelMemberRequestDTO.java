package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChannelMemberRequestDTO (
    @NotNull
    UUID userId,

    @NotNull
    UUID channelId
) {

}
