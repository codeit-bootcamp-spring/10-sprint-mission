package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChannelMemberRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID channelId;
}
